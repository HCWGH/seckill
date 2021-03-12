package com.myseckill.seckill.service;

import com.myseckill.seckill.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HCW
 * @since 2021-01-02
 */
public interface IOrderService extends IService<Order> {
  Order getOrderById(Long orderId);
}
