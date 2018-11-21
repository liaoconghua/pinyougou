package com.pinyougou.cart.service;

import java.util.Map;

/**
 * 微信支付服务接口
 *
 * @author Fa
 * @date 2018-10-29 17:21
 */
public interface WeiXinPayService {

    /**
     * 生成微信支付二维码
     *
     * @param outTradeNo 订单交易号
     * @param totalFee   金额(分)
     * @return 响应数据
     */
    Map<String, String> getPayCode(String outTradeNo, String totalFee);

    /**
     * 查询支付状态
     *
     * @param outTradeNo 订单交易号
     * @return 订单查询结果
     */
    Map<String, String> queryPayStatus(String outTradeNo);

    /**
     * 关闭超时未支付的订单
     *
     * @param outTradeNo 订单id
     * @return 关闭结果
     */
    Map<String, String> closeOrderPayByTimeout(String outTradeNo);
}
