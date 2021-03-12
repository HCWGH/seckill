package com.myseckill.seckill.dao;

import com.myseckill.seckill.pojo.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myseckill.seckill.vo.GoodsVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author HCW
 * @since 2021-01-02
 */
@Repository
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> getGoodsVo();

    GoodsVo getGoodsVoById(Long goods_id);
}
