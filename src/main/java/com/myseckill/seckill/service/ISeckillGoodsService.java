package com.myseckill.seckill.service;

import com.myseckill.seckill.pojo.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.myseckill.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HCW
 * @since 2021-01-02
 */
public interface ISeckillGoodsService extends IService<SeckillGoods> {

    int reduceStock(Long id);
    String createSeckillPath(User user,Long goodsId);
    boolean checkSeckillPath(String seckillPath,User user,Long goodsId);
    boolean checkCaptcha(User user,Long goodsId,String captcha);
}
