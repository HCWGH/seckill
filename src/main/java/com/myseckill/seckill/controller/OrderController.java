package com.myseckill.seckill.controller;



import com.myseckill.seckill.pojo.Order;
import com.myseckill.seckill.pojo.User;
import com.myseckill.seckill.service.IGoodsService;
import com.myseckill.seckill.service.IOrderService;
import com.myseckill.seckill.vo.GoodsVo;
import com.myseckill.seckill.vo.OrderDetailVo;
import com.myseckill.seckill.vo.ResponBean;
import com.myseckill.seckill.vo.ResponBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *订单详情
 * @author HCW
 * @since 2021-01-02
 */
@Controller
@RequestMapping("/order")
@ResponseBody
public class OrderController {
     @Autowired
     private IGoodsService goodsService;
     @Autowired
     private IOrderService orderService;
     @RequestMapping("/toOrderDetail")
     @ResponseBody
    public ResponBean toOrderDetail(User user,@RequestParam("orderId") Long orderId){
         if(user==null){
             return ResponBean.error(ResponBeanEnum.SESSION_ERROR);
         }
         Order order = orderService.getOrderById(orderId);
         GoodsVo goodsVo = goodsService.getGoodsVoById(order.getGoodsId());
         OrderDetailVo orderDetailVo = new OrderDetailVo();
         orderDetailVo.setGoodsVo(goodsVo);
         orderDetailVo.setOrder(order);
         orderDetailVo.setUser(user);
         return ResponBean.success(orderDetailVo);
    }
}
