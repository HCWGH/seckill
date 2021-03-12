package com.myseckill.seckill.service.impl;

import com.myseckill.seckill.pojo.Goods;
import com.myseckill.seckill.dao.GoodsMapper;
import com.myseckill.seckill.pojo.User;
import com.myseckill.seckill.service.IGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myseckill.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author HCW
 * @since 2021-01-02
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 返回商品列表
     * @return
     */
    @Override
    public List<GoodsVo> getGoodsVo() {
        return goodsMapper.getGoodsVo();
    }

    @Override
    public GoodsVo getGoodsVoById(Long goods_id) {
        return goodsMapper.getGoodsVoById(goods_id);
    }

}
