package com.pinyougou.seckill.task;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.WeiXinPayService;
import com.pinyougou.common.pojo.SeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 秒杀订单任务
 *
 * @author Fa
 * @date 2018-11-01 15:19
 */
@Component
public class SeckillOrderTask {
    @Reference(timeout = 10000)
    private SeckillOrderService seckillOrderService;
    @Reference(timeout = 10000)
    private WeiXinPayService weiXinPayService;

    /**
     * 定时关闭超时未支付定单(每隔3秒调度一次)
     */
    @Scheduled(cron = "0/3 * * * * ?")
    public void closeOrderTask() {
        List<SeckillOrder> seckillOrderList = seckillOrderService.findOrderByTimeout();
        for (SeckillOrder seckillOrder : seckillOrderList) {
            Map<String, String> resultMap = weiXinPayService.closeOrderPayByTimeout(
                    seckillOrder.getId().toString());
            if ("SUCCESS".equals(resultMap.get("result_code"))) {
                // 删除超时未支付的订单
                seckillOrderService.deleteOrderFromRedis(seckillOrder);
            }
        }
    }

}
