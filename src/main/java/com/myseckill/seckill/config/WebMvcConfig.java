package com.myseckill.seckill.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

/**
 * 处理Controller的方法自定义的参数
 * @author HCW
 * @date 2021/1/1-11:42
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig  implements WebMvcConfigurer {
     @Autowired
     private UserArgumentResolver argumentResolver;
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
       resolvers.add(argumentResolver);//传递User的参数处理器给对相应参数的处理器
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }
}
