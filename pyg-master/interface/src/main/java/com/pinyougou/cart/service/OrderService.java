package com.pinyougou.cart.service;

import com.pinyougou.common.pojo.Order;
import com.pinyougou.common.pojo.PayLog;

import java.util.HashMap;

/**
 * @author Fa
 * @date 2018-10-28 19:36
 */
public interface OrderService {

    /**
     * 保存订单
     *
     * @param order 订单
     */
    void save(Order order);

    /**
     * 保存我的订单
     *
     * @param order 订单
     */
    void saveMyOrder(Order order);

    /**
     * 根据用户查询支付日志
     *
     * @param userId 用户id(用户名)
     * @return 支付日志
     */
    PayLog findPayLogFromRedis(String userId);

    /**
     * 修改订单状态
     *
     * @param outTradeNo    订单交易号
     * @param transactionId 微信交易流水号
     */
    void updateOrderStatus(String outTradeNo, String transactionId);

    /**
     * 交易关闭
     *
     * @param orderId 订单id
     * @return 关闭结果
     */
    boolean closeOrder(String orderId);

    /**
     * 批量保存订单
     *
     * @param params 参数
     */
    void saveMyBatchOrder(HashMap<String, Object> params);
}
