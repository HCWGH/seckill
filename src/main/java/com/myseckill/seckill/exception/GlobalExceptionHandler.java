package com.myseckill.seckill.exception;

import com.myseckill.seckill.vo.ResponBean;
import com.myseckill.seckill.vo.ResponBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



/**
 * 全局异常处理
 * @author HCW
 * @date 2020/12/30-14:40
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponBean exceptionHandler(Exception e){
     if(e instanceof GlobalException){
         //全局异常
         GlobalException ex= (GlobalException) e;
         return ResponBean.error(ex.getResponBeanEnum());
     }else if(e instanceof BindException){
         //参数校验异常
         BindException ex= (BindException) e;
         ResponBean error = ResponBean.error(ResponBeanEnum.BIND_ERROR);
         //获取参数校验异常的默认输出信息，传给ResponBean对象
         error.setMessage("参数校验异常:"+ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
         return error;
     }
     //服务器异常
     return  ResponBean.error(ResponBeanEnum.ERROR);
    }

}
