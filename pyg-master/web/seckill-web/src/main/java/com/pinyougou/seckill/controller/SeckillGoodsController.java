package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.SeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Fa
 * @date 2018-10-31 16:35
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {
    @Reference(timeout = 10000)
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询所有秒杀商品
     *
     * @return 所有秒杀商品
     */
    @GetMapping("/findAllSeckillGoods")
    public List<SeckillGoods> findAllSeckillGoods() {
        try {
            return seckillGoodsService.findAllSeckillGoods();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询用户选择的商品
     *
     * @param id 商品id
     * @return 商品
     */
    @GetMapping("/findSeckillGoodsById")
    public SeckillGoods findSeckillGoodsById(Long id) {
        try {
            return seckillGoodsService.findSeckillGoodsFromRedis(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
