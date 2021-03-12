package com.myseckill.seckill.vo;

import com.myseckill.seckill.pojo.Order;
import com.myseckill.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单详情公共返回对象
 * @author HCW
 * @date 2021/1/8-20:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVo {
    private User user;
    private Order order;
    private GoodsVo goodsVo;
}
