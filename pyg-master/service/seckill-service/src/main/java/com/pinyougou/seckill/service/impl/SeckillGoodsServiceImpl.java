package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.pojo.SeckillGoods;
import com.pinyougou.seckill.mapper.SeckillGoodsMapper;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author Fa
 * @date 2018-10-31 16:33
 */
@Transactional
@Service(interfaceName = "com.pinyougou.seckill.service.SeckillGoodsService")
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询所有秒杀商品
     *
     * @return 秒杀商品集合
     */
    @Override
    public List<SeckillGoods> findAllSeckillGoods() {
        // 定义秒杀商品数据
        List<SeckillGoods> seckillGoodsList = null;
        try {
            // 从Redis中获取秒杀商品数据
            seckillGoodsList = redisTemplate.
                    boundHashOps("seckillGoodsList").values();
            if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
                System.out.println("Redis缓存中数据：" + seckillGoodsList);
                return seckillGoodsList;
            }

            //从数据库中查询数据
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            //审核通过
            criteria.andEqualTo("status", 1);
            criteria.andGreaterThan("stockCount", 0);
            criteria.andLessThanOrEqualTo("startTime", new Date());
            criteria.andGreaterThanOrEqualTo("endTime", new Date());
            seckillGoodsList = seckillGoodsMapper.selectByExample(example);
            //将秒杀商品存入redis中
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                redisTemplate.boundHashOps("seckillGoodsList").put(seckillGoods.getId(), seckillGoods);
            }
            return seckillGoodsList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询用户选择的商品
     *
     * @param id 商品id
     * @return 商品
     */
    @Override
    public SeckillGoods findSeckillGoodsFromRedis(Long id) {
        try {
            return (SeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
