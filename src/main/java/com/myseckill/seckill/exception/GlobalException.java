package com.myseckill.seckill.exception;

import com.myseckill.seckill.vo.ResponBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 自定义全局异常
 * @author HCW
 * @date 2020/12/30-14:38
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GlobalException extends RuntimeException {
    private ResponBeanEnum responBeanEnum;
}
