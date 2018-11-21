package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.pojo.SeckillGoods;
import com.pinyougou.common.pojo.SeckillOrder;
import com.pinyougou.common.utils.IdWorker;
import com.pinyougou.seckill.mapper.SeckillGoodsMapper;
import com.pinyougou.seckill.mapper.SeckillOrderMapper;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Fa
 * @date 2018-10-31 23:33
 */
@Transactional
@Service(interfaceName = "com.pinyougou.seckill.service.SeckillOrderService")
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 提交订单处理
     *
     * @param id       订单商品id
     * @param username 登录用户名
     * @return 处理结果
     */
    @Override
    public boolean submitOrderToRedis(Long id, String username) {
        try {
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(id);
            if (seckillGoods != null && seckillGoods.getStockCount() > 0) {
                seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
                if (seckillGoods.getStockCount() == 0) {
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                    redisTemplate.boundHashOps("seckillGoodsList").delete(id);
                } else {
                    redisTemplate.boundHashOps("seckillGoodsList").put(id, seckillGoods);
                }
                //创建订单对象
                SeckillOrder seckillOrder = new SeckillOrder();
                seckillOrder.setId(idWorker.nextId());
                seckillOrder.setSeckillId(id);
                seckillOrder.setMoney(seckillGoods.getCostPrice());
                seckillOrder.setUserId(username);
                seckillOrder.setSellerId(seckillGoods.getSellerId());
                seckillOrder.setCreateTime(new Date());
                seckillOrder.setStatus("0");
                redisTemplate.boundHashOps("seckillOrderList").put(username, seckillOrder);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询一个订单来自redis
     *
     * @return 订单
     */
    @Override
    public SeckillOrder findOrderFromRedis(String username) {
        try {
            return (SeckillOrder) redisTemplate.boundHashOps("seckillOrderList").get(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存订单到数据库
     *
     * @param userId        登录用户名
     * @param transactionId 微信交易流水号
     */
    @Override
    public void saveOrder(String userId, String transactionId) {
        try {
            SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("seckillOrderList").get(userId);
            if (seckillOrder != null) {
                seckillOrder.setTransactionId(transactionId);
                seckillOrder.setPayTime(new Date());
                seckillOrder.setStatus("1");
                seckillOrderMapper.insertSelective(seckillOrder);
                redisTemplate.boundHashOps("seckillOrderList").delete(userId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询所有超时未支付的订单
     *
     * @return 未支付订单集合
     */
    @Override
    public List<SeckillOrder> findOrderByTimeout() {
        //定义数据模型
        ArrayList<SeckillOrder> seckillOrders = new ArrayList<>();
        try {
            List<SeckillOrder> seckillOrderList = redisTemplate.boundHashOps("seckillOrderList").values();
            if (seckillOrderList != null && seckillOrderList.size() > 0) {
                for (SeckillOrder seckillOrder : seckillOrderList) {
                    //5分钟未支付的订单
                    if (System.currentTimeMillis() - seckillOrder.getCreateTime().getTime() > 5 * 60 * 1000) {
                        seckillOrders.add(seckillOrder);
                    }
                }
            }
            return seckillOrders;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从redis中删除未支付的订单
     *
     * @param seckillOrder 秒杀订单
     */
    @Override
    public void deleteOrderFromRedis(SeckillOrder seckillOrder) {
        redisTemplate.boundHashOps("seckillOrderList").delete(seckillOrder.getUserId());
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(
                "seckillGoodsList").get(seckillOrder.getSeckillId());
        if (seckillGoods != null) {
            seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
        } else {
            seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getSeckillId());
            seckillGoods.setStockCount(1);
        }
        redisTemplate.boundHashOps("seckillGoodsList").put(seckillOrder.getSeckillId(), seckillGoods);
    }
}
