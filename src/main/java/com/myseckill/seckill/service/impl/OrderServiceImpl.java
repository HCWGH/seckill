package com.myseckill.seckill.service.impl;

import com.myseckill.seckill.exception.GlobalException;
import com.myseckill.seckill.pojo.Order;
import com.myseckill.seckill.dao.OrderMapper;
import com.myseckill.seckill.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myseckill.seckill.vo.ResponBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author HCW
 * @since 2021-01-02
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
      @Autowired
      private OrderMapper orderMapper;
    @Override
    public Order getOrderById(Long orderId) {
        if(orderId==null){
            //订单不存在直接抛全局异常
            throw new GlobalException(ResponBeanEnum.ORDER_NO_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        return order;
    }
}
