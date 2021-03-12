package com.myseckill.seckill.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.util.concurrent.RateLimiter;
import com.myseckill.seckill.exception.GlobalException;
import com.myseckill.seckill.pojo.Order;
import com.myseckill.seckill.pojo.SeckillOrder;
import com.myseckill.seckill.pojo.User;
import com.myseckill.seckill.rabbitmq.MQSender;
import com.myseckill.seckill.rabbitmq.SeckillMessage;
import com.myseckill.seckill.service.IGoodsService;
import com.myseckill.seckill.service.ISeckillGoodsService;
import com.myseckill.seckill.service.ISeckillOrderService;
import com.myseckill.seckill.utils.JsonUtil;
import com.myseckill.seckill.vo.GoodsVo;
import com.myseckill.seckill.vo.ResponBean;
import com.myseckill.seckill.vo.ResponBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author HCW
 * @since 2021-01-02
 */
@Controller
@RequestMapping("/seckill")
public class SeckillGoodsController implements InitializingBean {
     @Autowired
     private IGoodsService goodsService;
     @Autowired
     private ISeckillOrderService iSeckillOrderService;
     @Autowired
     private RedisTemplate redisTemplate;
     @Autowired
     private MQSender mqSender;
     private HashMap<Long,Boolean> emptyStock=new HashMap<>();//内存标记该商品的数量减少访问Redis的次数
     @Autowired
     private DefaultRedisScript script;
     @Autowired
     private ISeckillGoodsService seckillGoodsService;

     private static Logger log=LoggerFactory.getLogger(SeckillGoodsController.class);
     //令牌桶限流，恒定的速度产生1000个令牌
     private RateLimiter rateLimiter=RateLimiter.create(1000);

    /**
     * 做秒杀
     * 压测结果：
     * 优化前QPS:241
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSeckill2")
    //@ResponseBody
    public String doSeckill2(Model model, User user, @RequestParam("goodsId") Long goodsId){
         if(user==null)return "login";
         //根据商品的ID，商品库存量
        model.addAttribute("user",user);
        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        //库存不足
         if(goodsVo.getStockCount()<=0){
             model.addAttribute("errmsg", ResponBeanEnum.EMPTY_STOCK.getMessage());
             return "seckillFail";
             //return ResponBean.error(ResponBeanEnum.EMPTY_STOCK);//进入秒杀失败页面
         }
         //判断是否是重复秒杀
        SeckillOrder seckillOrder = iSeckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
         if(seckillOrder!=null){
             //重复秒杀
             model.addAttribute("errmsg",ResponBeanEnum.REPEAT_DO_SECKILL.getMessage());
             return  "seckillFail";
             //return ResponBean.error(ResponBeanEnum.REPEAT_DO_SECKILL);
         }
         //进行秒杀，形成订单
        Order order = iSeckillOrderService.seckill(user, goodsVo);
         model.addAttribute("order",order);
         model.addAttribute("goods",goodsVo);
        return "orderDetail";
    }

    /**
     * 经过优化后的秒杀接口，秒杀静态化...
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    public ResponBean doSeckill(@PathVariable String path, Model model, User user, @RequestParam("goodsId") Long goodsId){
        if(user==null)return ResponBean.error(ResponBeanEnum.SESSION_ERROR);
        //根据商品的ID，商品库存量
        model.addAttribute("user",user);
        // GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        //判断是否是重复秒杀，秒杀成功的订单会缓存在Redis当中，避免了直接访问数据库
        ValueOperations operations = redisTemplate.opsForValue();

        //验证秒杀路径
        boolean checkResult = seckillGoodsService.checkSeckillPath(path, user, goodsId);
        if(!checkResult){
            return ResponBean.error(ResponBeanEnum.ILLEGAL_REQUEST);
        }
        String seckillOrder = (String) operations.get("order:" + user.getId() + ":" + goodsId);
        if(!StringUtils.isEmpty(seckillOrder)){
            return ResponBean.error(ResponBeanEnum.REPEAT_DO_SECKILL);
        }
        //hashMap标记商品的数量是否为空，减少Redis的访问
        if(emptyStock.get(goodsId)){
            return ResponBean.error(ResponBeanEnum.EMPTY_STOCK);
        }

        //预减Redis的库存
        //Long stock = operations.decrement("seckillGoods:" + goodsId);

        Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
        //库存小于0，该商品秒杀已经结束
        if(stock<=0){
            //operations.increment("seckillGoods:"+goodsId);
            emptyStock.put(goodsId,true);//标记该商品库存已经为空
            return ResponBean.error(ResponBeanEnum.EMPTY_STOCK);
        }
        //进行秒杀，形成订单
        //Order order = iSeckillOrderService.seckill(user, goodsVo);
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        //异步下单
        mqSender.sendsecKillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return ResponBean.success(0);
    }

    /**
     * 整个项目启动的时候把秒杀商品的数量预加载到Redis缓存当中
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos = goodsService.getGoodsVo();
        if(CollectionUtils.isEmpty(goodsVos)){
            return;
        }
        ValueOperations operations = redisTemplate.opsForValue();
        goodsVos.forEach(goodsVo -> {
            operations.set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount());
            emptyStock.put(goodsVo.getId(),false);
        });
    }

    /**
     *
     * @param user
     * @param goodsId
     * @return 获取秒杀结果 orderId:成功,-1:失败，0:排队
     */
    @RequestMapping(value = "/getSeckillResult",method = RequestMethod.GET)
    @ResponseBody
    public ResponBean getSeckillResult(User user,Long goodsId){
      if(user==null){
          return ResponBean.error(ResponBeanEnum.SESSION_TIMEOUT);
      }
        Long orderId = iSeckillOrderService.getSeckillResult(user, goodsId);
        return ResponBean.success(orderId);//返回秒杀订单的结果
    }

    /**
     *
     * @param user
     * @param goodsId
     * @return 获取秒杀的真正路径
     */
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public ResponBean getSeckillPath(User user, Long goodsId, String captcha, HttpServletRequest request){
        if(user==null){
            return  ResponBean.error(ResponBeanEnum.SESSION_TIMEOUT);
        }
        //guava令牌桶整体限流
        //6秒之内没抢到令牌，放弃当前抢购，可以重试抢购
        if(!rateLimiter.tryAcquire(1,TimeUnit.SECONDS)){
            return ResponBean.error(ResponBeanEnum.OPERATING_FAILURE_BY_HOT);
        }
        //单个用户限流，对一个用户可以在一秒钟之内最多点击5次秒杀按钮
        String uri = request.getRequestURI();
        ValueOperations operations = redisTemplate.opsForValue();

        Integer clickCounts = (Integer) operations.get(uri + ":" + user.getId() + ":" + goodsId);
        //为null说明该用户是第一次访问
        if(clickCounts==null){
            operations.set(uri+":"+user.getId()+":"+goodsId,1,5,TimeUnit.SECONDS);
        }else if(clickCounts<5){
            operations.increment(uri+":"+user.getId()+":"+goodsId);//在允许的次数内访问，访问次数+1
        }else{
            return  ResponBean.error(ResponBeanEnum.ACCESS_FREQUENTLY);
        }
        //图像验证码校验
        boolean checkCaptchaResult = seckillGoodsService.checkCaptcha(user, goodsId, captcha);
        if(!checkCaptchaResult){
            return ResponBean.error(ResponBeanEnum.VERIFICATION_CODE_ERROR);
        }
        //再真正去生成对应的秒杀路径
        String seckillPath = seckillGoodsService.createSeckillPath(user, goodsId);
        return ResponBean.success(seckillPath);
    }

    /**
     *生成验证码图片並且确认接过
     * @param user
     * @param goodsId
     * @param response
     */
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if (null == user || goodsId < 0) {
            throw new GlobalException(ResponBeanEnum.ILLEGAL_REQUEST);
        }
        // 设置请求头为输出图片类型
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码，将结果放入redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }
    }
}


