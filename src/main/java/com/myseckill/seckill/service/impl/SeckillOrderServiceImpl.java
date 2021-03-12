package com.myseckill.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.myseckill.seckill.dao.OrderMapper;
import com.myseckill.seckill.pojo.Order;
import com.myseckill.seckill.pojo.SeckillGoods;
import com.myseckill.seckill.pojo.SeckillOrder;
import com.myseckill.seckill.dao.SeckillOrderMapper;
import com.myseckill.seckill.pojo.User;
import com.myseckill.seckill.service.ISeckillGoodsService;
import com.myseckill.seckill.service.ISeckillOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myseckill.seckill.utils.JsonUtil;
import com.myseckill.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author HCW
 * @since 2021-01-02
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {
     @Autowired
     private ISeckillGoodsService goodsService;
     @Autowired
     private OrderMapper orderMapper;
     @Autowired
     private SeckillOrderMapper seckillOrderMapper;
     @Autowired
     private RedisTemplate redisTemplate;

    /**
     * 从RabbitMQ得到消息，真正的数据库减库存操作
     * @param user
     * @param goodsVo
     * @return
     */
    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goodsVo) {
        ValueOperations operations = redisTemplate.opsForValue();

        //库存减一，在商品数量大于0时再减库存
        boolean seckillResult = goodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count=stock_count-1").eq("goods_id", goodsVo.getId()).gt("stock_count", 0));
        //int reduceStock = goodsService.reduceStock(goodsVo.getId());
        //减库存成功
        if(seckillResult){
            //生成订单
            Order order = new Order();
            order.setCreaateDate(new Date());
            order.setDeliveryAddId1(1L);
            order.setGoodsCount(1);
            order.setGoodsId(goodsVo.getId());
            order.setGoodsName(goodsVo.getGoodsName());
            order.setGoodsPrice(goodsVo.getGoodsPrice());
            order.setGoodsId(goodsVo.getId());
            order.setOrderChannel(0);
            order.setOrderStatus(0);
            order.setPayDate(new Date());
            order.setUserId(user.getId());
            orderMapper.insert(order);
            //生成秒杀订单
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setGoodsId(goodsVo.getId());
            seckillOrder.setOrderId(order.getId());
            seckillOrder.setUserId(user.getId());
            seckillOrderMapper.insert(seckillOrder);
            //秒杀数据减库存成功，把该用户已经秒杀过该产品记录在Redis当中，用来判断其防止重复秒杀
            operations.set("order:"+user.getId()+":"+goodsVo.getId(), JsonUtil.object2JsonStr(seckillOrder));
            return order;
        }else{
            //生成订单失败，因为库存数量=0
            operations.set("isStockEmpty:"+goodsVo.getId(),0);
            return null;
        }

    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public Long getSeckillResult(User user, Long goodsId) {
        Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if(order!=null){
            //秒杀成功，轮询到结果，返回订单的id
            return order.getId();
        }else{
            //秒杀没有形成订单
            if(redisTemplate.hasKey("isStockEmpty:"+goodsId)){
                //因为库存不足导致秒杀失败，没有形成订单
                return -1L;
            }else{
                //因为队列的原因在排队，还没形成订单
                return 0L;
            }
        }
    }
}
