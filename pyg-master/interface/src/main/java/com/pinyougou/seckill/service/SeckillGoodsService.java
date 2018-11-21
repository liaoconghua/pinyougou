package com.pinyougou.seckill.service;

import com.pinyougou.common.pojo.SeckillGoods;

import java.util.List;

/**
 * @author Fa
 * @date 2018-10-31 16:34
 */
public interface SeckillGoodsService {
    /**
     * 查询所有秒杀商品
     *
     * @return 秒杀商品集合
     */
    List<SeckillGoods> findAllSeckillGoods();

    /**
     * 查询用户选择的商品
     *
     * @param id 商品id
     * @return 商品
     */
    SeckillGoods findSeckillGoodsFromRedis(Long id);
}
