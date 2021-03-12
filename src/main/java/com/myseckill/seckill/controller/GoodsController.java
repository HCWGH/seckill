package com.myseckill.seckill.controller;

import com.myseckill.seckill.pojo.User;
import com.myseckill.seckill.service.IGoodsService;
import com.myseckill.seckill.service.impl.UserServiceImpl;
import com.myseckill.seckill.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author HCW
 * @date 2020/12/30-21:39
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 压测结果：
     * 优化前:5000*10*3 ：QPS=438左右
     * 优化后:5000*10*3: QPS=900.8左右
     * 返回商品列表
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/toGoodsList",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toGoodsList(HttpServletRequest request, HttpServletResponse response, Model model,User user/*@CookieValue("userToken") String token*/){

     /* if(StringUtils.isEmpty(token)){
          return  "login";
      }else{
          //从session里面拿到User的信息,实现分布式session
          //User user = (User) session.getAttribute(token);
          //Redis代替解决分布式Session问题
          User user = userService.getUserByCookie(request, response, token);
          if(user==null){
              return  "login";
          }
          model.addAttribute("user",user);
          return "goodsList";
      }*/
        //redis实现页面缓存
        ValueOperations opsForValue = redisTemplate.opsForValue();
        String html = (String) opsForValue.get(CacheName.GOODS_LIST.getName());
        //Redis缓存中存在该页面缓存
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //Redis缓存当中不存在该页面缓存
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.getGoodsVo());
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process(CacheName.GOODS_LIST.getName(), context);
        if(!StringUtils.isEmpty(html)){
            opsForValue.set(CacheName.GOODS_LIST.getName(),html,60, TimeUnit.SECONDS);
        }

        return html;
}

    /**
     * 返回某个商品的具体详情
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/toGoodsDetail/{goodsId}",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toGoodsDetail2(HttpServletResponse response,HttpServletRequest request,Model model, User user, @PathVariable Long goodsId){

        //查询Redis缓存当中有没有对应的商品详情页面缓存
        ValueOperations opsForValue = redisTemplate.opsForValue();
        String html = (String) opsForValue.get(CacheName.GOODS_DETAIL.getName() +":"+ goodsId);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
     GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
    Date startDate = goodsVo.getStartDate();
    Date endDate = goodsVo.getEndDate();
    Date nowdate = new Date();
    //距离秒杀开始的时间
    int remainSeconds=0;
    //商品的秒杀状态 0.秒杀未开始 1.秒杀进行中 2.秒杀已结束
    int secKillStatus=0;
    if(nowdate.before(startDate)){
        //秒杀还未开始
        remainSeconds= (int) (startDate.getTime()-nowdate.getTime())/1000;
    }else if(nowdate.after(endDate)){
        //秒杀已经结束
        secKillStatus=2;
        remainSeconds=-1;
    }else{
        //秒杀进行中
        secKillStatus=1;
        remainSeconds=0;
    }
    model.addAttribute("user",user);
    model.addAttribute("goods",goodsVo);
    model.addAttribute("secKillStatus",secKillStatus);
    model.addAttribute("remainSeconds",remainSeconds);
    //跟商品Id把商品的详情存入Redis缓存当中
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
       html = thymeleafViewResolver.getTemplateEngine().process(CacheName.GOODS_DETAIL.getName(), context);
       if(!StringUtils.isEmpty(html)){
           opsForValue.set(CacheName.GOODS_DETAIL.getName()+":"+goodsId,html,60,TimeUnit.SECONDS);
       }
        return html;
}

    /**
     * 页面静态化时候用，静态不需要渲染真个html页面，只需要从后台获取数据，填充html页面即可
     *
     * 页面静态化和未静态化的对比***：
     *
     * 未作页面静态化：请求某一个页面，访问缓存，查看缓存中是否有，缓存中有直接返回，缓存中没有的话，
     * 将数据渲染到html页面再存到缓存，再将整个html页面返回给客户端显示。
     *
     * 做了页面静态化：第一次是去请求后台要渲染好的html页面，之后的请求都是直接访问用户本地浏览器的缓存的html页面 静态资源，
     * 然后前端通过Ajax来访问后端，只去获取页面需要显示的数据返回即可。
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/toDetail/{goodsId}")
    @ResponseBody
    public ResponBean toGoodsDetail( User user, @PathVariable Long goodsId){

       //用户没有登陆
       if(user==null){
           return ResponBean.error(ResponBeanEnum.SESSION_TIMEOUT);
       }
        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowdate = new Date();
        //距离秒杀开始的时间
        int remainSeconds=0;
        //商品的秒杀状态 0.秒杀未开始 1.秒杀进行中 2.秒杀已结束
        int secKillStatus=0;
        if(nowdate.before(startDate)){
            //秒杀还未开始
            remainSeconds= (int) (startDate.getTime()-nowdate.getTime())/1000;
        }else if(nowdate.after(endDate)){
            //秒杀已经结束
            secKillStatus=2;
            remainSeconds=-1;
        }else{
            //秒杀进行中
            secKillStatus=1;
            remainSeconds=0;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setGoodsVo(goodsVo);
        detailVo.setRemainSeconds(remainSeconds);
        detailVo.setSeckillStatus(secKillStatus);
        detailVo.setUser(user);
        return ResponBean.success(detailVo);
    }
}
