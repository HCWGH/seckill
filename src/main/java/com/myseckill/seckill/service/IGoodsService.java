package com.myseckill.seckill.service;

import com.myseckill.seckill.pojo.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.myseckill.seckill.pojo.Order;
import com.myseckill.seckill.pojo.User;
import com.myseckill.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HCW
 * @since 2021-01-02
 */
public interface IGoodsService extends IService<Goods> {
    /**
     * 返回商品列表
     * @return
     */
    List<GoodsVo> getGoodsVo();

    /**
     根据商品的goods_id查询对应商品的详情
     * @param goods_id
     * @return
     */
    GoodsVo getGoodsVoById(Long goods_id);
}
