package com.myseckill.seckill.service.impl;

import com.myseckill.seckill.pojo.SeckillGoods;
import com.myseckill.seckill.dao.SeckillGoodsMapper;
import com.myseckill.seckill.pojo.User;
import com.myseckill.seckill.service.ISeckillGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myseckill.seckill.utils.MD5;
import com.myseckill.seckill.utils.UuidUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author HCW
 * @since 2021-01-02
 */
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements ISeckillGoodsService {
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public int reduceStock(Long id) {
        int stock = seckillGoodsMapper.reduceStock(id);
        return stock;
    }

    /**
     * 生成秒杀路径
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createSeckillPath(User user, Long goodsId) {
        String path = MD5.md5(UuidUtil.getUuid() + "627627");
        redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"+goodsId,path,60, TimeUnit.SECONDS);
        return path;
    }

    /**
     * 做秒杀的时候对路径进行检查
     * @param seckillPath
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public boolean checkSeckillPath(String seckillPath, User user, Long goodsId) {
        String path = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(seckillPath);
    }

    /**
     * 图形码验证
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(user==null|| StringUtils.isEmpty(captcha)||goodsId<0){
            return false;
        }
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
