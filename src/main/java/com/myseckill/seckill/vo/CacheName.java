package com.myseckill.seckill.vo;

import lombok.*;

/**
 * @author HCW
 * @date 2021/1/4-14:00
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public enum CacheName {
    GOODS_LIST("goodsList"),
    GOODS_DETAIL("goodsDetail"),
    USER_CACHE("user"),
    ;
    private String name;

}
