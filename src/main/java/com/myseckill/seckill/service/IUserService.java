package com.myseckill.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myseckill.seckill.pojo.User;
import com.myseckill.seckill.vo.LoginVo;
import com.myseckill.seckill.vo.ResponBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HCW
 * @since 2020-12-29
 */
public interface IUserService extends IService<User> {

    ResponBean doLogin(LoginVo loginVo, HttpServletResponse response, HttpServletRequest httpServletResponse);
    User getUserByCookie(HttpServletRequest request,HttpServletResponse response,String token);
}
