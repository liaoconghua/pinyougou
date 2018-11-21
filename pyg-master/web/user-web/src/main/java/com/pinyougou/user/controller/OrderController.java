package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.OrderService;
import com.pinyougou.cart.service.WeiXinPayService;
import com.pinyougou.common.pojo.Order;
import com.pinyougou.common.pojo.PayLog;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Fa
 * @date 2018-10-28 19:34
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference(timeout = 10000)
    private OrderService orderService;
    /**
     * 微信支付服务接口
     */
    @Reference(timeout = 10000)
    private WeiXinPayService weiXinPayService;

    /**
     * 保存订单
     */
    @PostMapping("/save")
    public boolean save(@RequestBody Order order,
                        HttpServletRequest request) {
        try {
            // 获取登录用户名
            String userId = request.getRemoteUser();
            order.setUserId(userId);
            // 设置订单来源 PC端
            order.setSourceType("2");
            orderService.saveMyOrder(order);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 批量保存订单
     *
     * @return 保存结果
     */
    @GetMapping("/mergeOrder")
    public boolean mergeOrder(String[] ids, HttpServletRequest request) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        try {
            // 获取登录用户名
            String userId = request.getRemoteUser();
            params.put("userId", userId);
            orderService.saveMyBatchOrder(params);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 生成微信支付二维码
     *
     * @return 响应数据
     */
    @GetMapping("/getPayCode")
    public Map<String, String> getPayCode(HttpServletRequest request) {
        //获取登录用户名
        String userId = request.getRemoteUser();
        //从Redis查询支付日志
        PayLog payLog = orderService.findPayLogFromRedis(userId);
        //调用生成微信支付二维码服务方法
        return weiXinPayService.getPayCode(payLog.getOutTradeNo(), String.valueOf(payLog.getTotalFee()));
    }

    /**
     * 查询支付状态
     *
     * @param outTradeNo 支付订单号
     * @return 支付结果状态码
     */
    @GetMapping("/queryPayStatus")
    public Map<String, Integer> queryPayStatus(String outTradeNo) {
        Map<String, Integer> data = new HashMap<>();
        data.put("status", 3);
        try {
            // 调用查询订单接口
            Map<String, String> resMap = weiXinPayService.queryPayStatus(outTradeNo);
            if (resMap != null && resMap.size() > 0) {
                // 判断是否支付成功
                if ("SUCCESS".equals(resMap.get("trade_state"))) {
                    //修改订单状态
                    orderService.updateOrderStatus(outTradeNo,
                            resMap.get("transaction_id"));
                    data.put("status", 1);
                }
                if ("NOTPAY".equals(resMap.get("trade_state"))) {
                    data.put("status", 2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 关闭订单
     *
     * @param orderId 订单id
     * @return 关闭结果
     */
    @GetMapping("/closeOrder")
    public boolean closeOrder(String orderId) {
        try {
            return orderService.closeOrder(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
