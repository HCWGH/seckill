package com.myseckill.seckill.controller;


import com.myseckill.seckill.pojo.User;
import com.myseckill.seckill.vo.ResponBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author HCW
 * @since 2020-12-29
 */
@Controller
@RequestMapping("/user")
public class UserController {
    /**
     * jmeter压测用户信息
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public ResponBean info(User user){
        System.out.println(user+"===========");
        return ResponBean.success(user);
    }
}
