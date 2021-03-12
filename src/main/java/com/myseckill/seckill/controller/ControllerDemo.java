package com.myseckill.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author HCW
 * @date 2020/12/28-22:19
 */
@Controller
@RequestMapping("/demo")
public class ControllerDemo {
    @RequestMapping("/hello")
    public String he(Model model){
        model.addAttribute("name","ddd");
        return  "hello";
    }
}
