package com.myseckill.seckill.vo;

import com.myseckill.seckill.validator.IsMobile;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author HCW
 * @date 2020/12/30-10:00
 */
@Data
@ToString
public class LoginVo {
    @NotNull
    @IsMobile
    private String mobile;
    @NotNull
    @Length(min = 32)
    private String password;
}
