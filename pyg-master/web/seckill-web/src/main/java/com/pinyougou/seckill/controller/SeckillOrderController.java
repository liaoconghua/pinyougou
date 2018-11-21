package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.WeiXinPayService;
import com.pinyougou.common.pojo.SeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Fa
 * @date 2018-10-31 22:08
 */
@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {
    @Reference(timeout = 10000)
    private SeckillOrderService seckillOrderService;
    @Reference(timeout = 10000)
    private WeiXinPayService weiXinPayService;

    /**
     * 保存订单
     *
     * @param id 当前商品id
     * @return 保存结果
     */
    @GetMapping("/submitOrder")
    public boolean submitOrder(Long id, HttpServletRequest request) {
        try {
            String username = request.getRemoteUser();
            return seckillOrderService.submitOrderToRedis(id, username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 生成支付二维码
     *
     * @return 结果集
     */
    @GetMapping("/genPayCode")
    public Map<String, String> genPayCode(HttpServletRequest request) {
        String username = request.getRemoteUser();
        SeckillOrder seckillOrder = seckillOrderService.findOrderFromRedis(username);
        return weiXinPayService.getPayCode(seckillOrder.getId().toString(),
                String.valueOf((long) (seckillOrder.getMoney().doubleValue() * 100)));
    }

    /**
     * 查询支付状态
     *
     * @param outTradeNo 商户订单号
     * @return 查询结果
     */
    @GetMapping("/queryPayStatus")
    public Map<String, Integer> queryPayStatus(String outTradeNo, HttpServletRequest request) {
        HashMap<String, Integer> payStatus = new HashMap<>(1);
        try {
            payStatus.put("status", 3);
            Map<String, String> queryPayStatus = weiXinPayService.queryPayStatus(outTradeNo);
            if (queryPayStatus != null && queryPayStatus.size() > 0) {
                //支付成功
                if ("SUCCESS".equals(queryPayStatus.get("trade_state"))) {
                    // 获取登录用户名
                    String userId = request.getRemoteUser();
                    // 保存订单到数据库
                    seckillOrderService.saveOrder(userId,
                            queryPayStatus.get("transaction_id"));
                    payStatus.put("status", 1);
                } else {//未支付
                    payStatus.put("status", 2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payStatus;
    }
}
