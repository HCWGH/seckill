package com.myseckill.seckill.validator;

import com.myseckill.seckill.utils.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * JSR303参数校验异常注解实现类
 * @author HCW
 * @date 2020/12/30-14:21
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {

    private boolean required=false;
    @Override
    public void initialize(IsMobile constraintAnnotation) {
         required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(required){
            return ValidatorUtil.isMobile(value);
        }else{
            if(StringUtils.isEmpty(value)){
                return  true;
            }else{
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
