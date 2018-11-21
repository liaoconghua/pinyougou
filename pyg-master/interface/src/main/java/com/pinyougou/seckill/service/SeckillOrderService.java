package com.pinyougou.seckill.service;

import com.pinyougou.common.pojo.SeckillOrder;

import java.util.List;

/**
 * @author Fa
 * @date 2018-10-31 23:29
 */
public interface SeckillOrderService {

    /**
     * 提交订单处理
     *
     * @param id       订单商品id
     * @param username 登录用户名
     * @return 处理结果
     */
    boolean submitOrderToRedis(Long id, String username);

    /**
     * 查询一个订单来自redis
     *
     * @param username 用户名
     * @return 订单
     */
    SeckillOrder findOrderFromRedis(String username);

    /**
     * 保存订单到数据库
     *
     * @param userId        登录用户名
     * @param transactionId 微信交易流水号
     */
    void saveOrder(String userId, String transactionId);

    /**
     * 查询所有超时未支付的订单
     *
     * @return 未支付订单集合
     */
    List<SeckillOrder> findOrderByTimeout();

    /**
     * 从redis中删除未支付的订单
     *
     * @param seckillOrder 秒杀订单
     */
    void deleteOrderFromRedis(SeckillOrder seckillOrder);
}
