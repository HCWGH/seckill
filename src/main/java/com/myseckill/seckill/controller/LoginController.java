package com.myseckill.seckill.controller;

import com.myseckill.seckill.service.IUserService;
import com.myseckill.seckill.vo.LoginVo;
import com.myseckill.seckill.vo.ResponBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author HCW
 * @date 2020/12/30-8:47
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {
    @Autowired
    private IUserService userService;

    /**
     * 跳转到登录页
     * @return
     */
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    /**
     * 点击登录
     * @param loginVo
     * @return
     */
    @RequestMapping("/doLogin")
    @ResponseBody
    public ResponBean login(@Valid LoginVo loginVo, HttpServletResponse response, HttpServletRequest request){
        log.info(loginVo.toString());
        return  userService.doLogin(loginVo,response,request);
    }
}
