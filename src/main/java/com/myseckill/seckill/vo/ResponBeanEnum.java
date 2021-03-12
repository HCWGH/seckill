package com.myseckill.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**公共返回对象的枚举
 * @author HCW
 * @date 2020/12/30-9:03
 */
@Getter
@ToString
@AllArgsConstructor
public enum  ResponBeanEnum {
    //通用模块
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务器异常"),
    //登录模块
    SESSION_ERROR(500210,"session不存在或者已经失效"),
    LOGINVO_ERROR(500211,"用户名或者密码错误"),
    MOBILE_ERROR(500212,"手机号码格式错误"),
    BIND_ERROR(500213,"参数校验异常"),
    //秒杀模块5003XX
    EMPTY_STOCK(500300,"库存不足"),
    REPEAT_DO_SECKILL(500301,"重复秒杀"),
    SESSION_TIMEOUT(500302,"会话过期"),
    ORDER_NO_EXIST(500303,"订单不存在"),
    ILLEGAL_REQUEST(500304,"非法请求"),
    VERIFICATION_CODE_ERROR(500305,"验证码错误"),
    ACCESS_FREQUENTLY(500306,"访问次数频繁"),
    OPERATING_FAILURE_BY_HOT(500307,"抢购失败,商品过于或火爆,请重新抢购"),
    ;
    private  Integer code;
    private   String message;

}
