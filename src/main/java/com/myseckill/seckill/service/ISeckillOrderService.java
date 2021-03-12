package com.myseckill.seckill.service;

import com.myseckill.seckill.pojo.Order;
import com.myseckill.seckill.pojo.SeckillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.myseckill.seckill.pojo.User;
import com.myseckill.seckill.vo.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HCW
 * @since 2021-01-02
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    Order seckill(User user, GoodsVo goodsVo);

    Long getSeckillResult(User user, Long goodsId);
}
