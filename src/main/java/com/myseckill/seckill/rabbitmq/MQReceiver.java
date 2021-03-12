package com.myseckill.seckill.rabbitmq;


import com.myseckill.seckill.pojo.User;
import com.myseckill.seckill.service.IGoodsService;
import com.myseckill.seckill.service.ISeckillOrderService;
import com.myseckill.seckill.utils.JsonUtil;
import com.myseckill.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {
  @Autowired
  private IGoodsService goodsService;
  @Autowired
  private RedisTemplate redisTemplate;
  @Autowired
  private ISeckillOrderService iSeckillOrderService;
  @RabbitListener(queues = "seckillQueue")
  public void receive(String msg) {
   log.info("QUEUE接受消息：" + msg);
   SeckillMessage message = JsonUtil.jsonStr2Object(msg, SeckillMessage.class);
   Long goodsId = message.getGoodsId();
   User user = message.getUser();
   GoodsVo goods = goodsService.getGoodsVoById(goodsId);
   //判断库存
   if (goods.getStockCount() < 1) {
     return ;
   }
   //这里再判断是否重复秒杀，防止消息队列的延迟导致第一层判断Redis当中还没有对应的订单
   String seckillOrderJson = (String) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
   if (!StringUtils.isEmpty(seckillOrderJson)) {
       redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);//重复秒杀把Redis商品数量加回去，保证数据库和Redis商品数量一致性
     return;
   }
   //orderService.seckill(user, goods);
   //真正的减库存和生成订单
   iSeckillOrderService.seckill(user, goods);

  }
}