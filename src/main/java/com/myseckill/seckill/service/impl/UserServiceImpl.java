package com.myseckill.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myseckill.seckill.dao.UserMapper;
import com.myseckill.seckill.exception.GlobalException;
import com.myseckill.seckill.pojo.User;
import com.myseckill.seckill.service.IUserService;
import com.myseckill.seckill.utils.CookieUtil;
import com.myseckill.seckill.utils.MD5;
import com.myseckill.seckill.utils.UuidUtil;
import com.myseckill.seckill.vo.CacheName;
import com.myseckill.seckill.vo.LoginVo;
import com.myseckill.seckill.vo.ResponBean;
import com.myseckill.seckill.vo.ResponBeanEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author HCW
 * @since 2020-12-29
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
      @Autowired
      private UserMapper userMapper;
      @Autowired
      private RedisTemplate redisTemplate;

    /**
     * 登录时的参数检验以及身份校验
     * @param loginVo
     * @param response
     * @param request
     * @return
     */
    @Override
    public ResponBean doLogin(LoginVo loginVo, HttpServletResponse response, HttpServletRequest request) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        User user = userMapper.selectById(mobile);
        if(user==null){
            //手机号输入错误
           throw  new GlobalException(ResponBeanEnum.LOGINVO_ERROR);
        }else{
            String salt = user.getSalt();
            String backstageToDB = MD5.backstageToDB(password, salt);
            if(user.getPassword().equals(backstageToDB)){
                //用户信息设置到session里面去
                String token = UuidUtil.getUuid();
                //request.getSession().setAttribute(token,user);//用户的信息保存到session里面
                //Redis代替解决分布式session的问题
                redisTemplate.opsForValue().set(CacheName.USER_CACHE.getName()+":" +token,user);
                CookieUtil.setCookie(request,response,"userToken",token);
                return ResponBean.success(token);
            }else{
                //密码输入错误
              throw  new GlobalException(ResponBeanEnum.LOGINVO_ERROR);
            }

        }
    }

    /**
     *Cookie获取用户的个人信息，Cookie给Redis传递userToken
     * @param request
     * @param response
     * @param token
     * @return
     */
    @Override
    public User getUserByCookie(HttpServletRequest request, HttpServletResponse response, String token) {
        if(StringUtils.isEmpty(token)){
            return  null;
        }
        User user = (User) redisTemplate.opsForValue().get(CacheName.USER_CACHE.getName()+":"  + token);
        if(user!=null){
            CookieUtil.setCookie(request,response,"userToken",  token);
        }
        return user;
    }
}
