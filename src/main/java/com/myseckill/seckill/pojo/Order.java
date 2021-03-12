package com.myseckill.seckill.pojo;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author HCW
 * @since 2021-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 收货地址id
     */
    private Long deliveryAddId1;

    /**
     * 冗余的商品名称便于查询
     */
    private String goodsName;

    /**
     * 购买的商品数量
     */
    private Integer goodsCount;

    /**
     * 商品的价格
     */
    private BigDecimal goodsPrice;

    /**
     * 下单渠道 1.pc 2.android 3.ios
     */
    private Integer orderChannel;

    /**
     * 订单的状态 0.未支付 1.已支付 2.已发货..... 
     */
    private Integer orderStatus;

    /**
     * 下单时间
     */
    private Date creaateDate;

    /**
     * 支付时间
     */
    private Date payDate;


}
