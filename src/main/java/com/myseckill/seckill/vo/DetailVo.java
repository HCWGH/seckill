package com.myseckill.seckill.vo;

import com.myseckill.seckill.pojo.User;
import lombok.*;

/**
 * 商品详情页面的Vo
 * @author HCW
 * @date 2021/1/4-21:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class DetailVo {
    private User user;
    private GoodsVo goodsVo;
    private int seckillStatus;
    private int remainSeconds;
}
