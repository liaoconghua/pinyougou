package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.Cart;
import com.pinyougou.cart.mapper.OrderItemMapper;
import com.pinyougou.cart.mapper.OrderMapper;
import com.pinyougou.cart.mapper.PayLogMapper;
import com.pinyougou.cart.service.OrderService;
import com.pinyougou.common.pojo.Order;
import com.pinyougou.common.pojo.OrderItem;
import com.pinyougou.common.pojo.PayLog;
import com.pinyougou.common.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Fa
 * @date 2018-10-28 19:38
 */
@Transactional
@Service(interfaceName = "com.pinyougou.cart.service.OrderService")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private PayLogMapper payLogMapper;

    /**
     * 保存订单
     *
     * @param order 订单
     */
    @Override
    public void save(Order order) {
        try {
            // 根据用户名获取Redis中购物车数据
            List<Cart> carts = (List<Cart>) redisTemplate.boundValueOps("cart_" + order.getUserId() + "_selected").get();

            //定义订单ID集合(一次支付对应多个订单)
            List<String> orderIdList = new ArrayList<>();

            // 迭代购物车数据,将数据持久化到数据库中
            double totalMoney = insertDataAndGetTotalMoney(order, carts, orderIdList);

            //判断是否为微信支付
            payTypeIsWX(order, orderIdList, totalMoney);

            // 取出用户未选中剩下的购物车订单
            List<Cart> remainCartList = (List<Cart>) redisTemplate.boundValueOps("cart_" + order.getUserId() + "_remain").get();
            if (remainCartList != null && remainCartList.size() > 0) {
                //覆盖用户原购物车数据
//                redisTemplate.delete("cart_" + order.getUserId());
                redisTemplate.boundValueOps("cart_" + order.getUserId()).set(remainCartList);
                //删除用户购物车未选中订单
                redisTemplate.delete("cart_" + order.getUserId() + "_remain");
            } else {
                // 删除该用户购物车数据
                redisTemplate.delete("cart_" + order.getUserId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 保存订单
     *
     * @param order 订单
     */
    @Override
    public void saveMyOrder(Order order) {
        try {
            // 根据用户名获取Redis中购物车数据
//            List<Cart> carts = (List<Cart>) redisTemplate.boundValueOps("cart_" + order.getUserId() + "_selected").get();

            //定义订单ID集合(一次支付对应多个订单)
//            List<String> orderIdList = new ArrayList<>();

            // 迭代购物车数据,将数据持久化到数据库中
            //定义多个订单支付的总金额（元）
//            double totalMoney = 0;
//            for (Cart cart : carts) {
            /* ####### 往订单表插入数据 ######### */
            // 生成订单主键id
//                long orderId = idWorker.nextId();
            // 创建新的订单
//                Order orderRow = new Order();
//                orderRow.setOrderId(orderId);
            // 设置支付类型
//                orderRow.setPaymentType(order.getPaymentType());
            // 设置支付状态码为“未支付”
//                orderRow.setStatus("1");
//                orderRow.setCreateTime(new Date());
//                orderRow.setUpdateTime(orderRow.getCreateTime());
//                orderRow.setUserId(order.getUserId());
//                orderRow.setReceiverAreaName(order.getReceiverAreaName());
            // 设置收件人地址
//                orderRow.setReceiverMobile(order.getReceiverMobile());
//                orderRow.setReceiver(order.getReceiver());
//                orderRow.setSourceType(order.getSourceType());
//                orderRow.setSellerId(cart.getSellerId());

            // 定义该订单总金额
//                double money = 0;
            /* ####### 往订单明细表插入数据(当前商家下的子订单) ######### */
//                for (OrderItem orderItem : cart.getOrderItems()) {
//                    // 设置主键id
//                    orderItem.setId(idWorker.nextId());
//                    // 设置关联的订单id
//                    orderItem.setOrderId(orderId);
//                    // 累计该商家总金额
//                    money += orderItem.getTotalFee().doubleValue();
//                    // 保存数据到订单明细表
//                    orderItemMapper.insertSelective(orderItem);
//                }
            // 设置支付总金额(一个商家的订单总金额)
//                orderRow.setPayment(new BigDecimal(money));
            // 保存数据到订单表
//                orderMapper.insertSelective(orderRow);

            //记录订单id
//                orderIdList.add(String.valueOf(orderId));
            //记录所有商家总金额（付款总金额）
//                totalMoney += money;
//            }

            //判断是否为微信支付
            if ("1".equals(order.getPaymentType())) {
                //创建支付日志对象
                PayLog payLog = new PayLog();
                //生成订单交易号
                String outTradeNo = String.valueOf(idWorker.nextId());
                //设置订单交易号
                payLog.setOutTradeNo(outTradeNo);
                //创建时间
                payLog.setCreateTime(new Date());
                //支付总金额(分)
                payLog.setTotalFee((long) (order.getPayment().doubleValue() * 100));
                //用户ID
                payLog.setUserId(order.getUserId());
                //支付状态
                payLog.setTradeState("0");
                //订单号集合，逗号分隔
//                String ids = orderIdList.toString().replace("[", "")
//                        .replace("]", "").replace(" ", "");
                //设置订单号
                payLog.setOrderList(order.getOrderId().toString());
                //支付类型
                payLog.setPayType("1");
                //往支付日志表插入数据
                payLogMapper.insertSelective(payLog);
                //存入缓存
                redisTemplate.boundValueOps("payLog_" + order.getUserId()).set(payLog);
            }

            // 取出用户未选中剩下的购物车订单
//            List<Cart> remainCartList = (List<Cart>) redisTemplate.boundValueOps("cart_" + order.getUserId() + "_remain").get();
//            if (remainCartList != null && remainCartList.size() > 0) {
            //覆盖用户原购物车数据
//                redisTemplate.delete("cart_" + order.getUserId());
//                redisTemplate.boundValueOps("cart_" + order.getUserId()).set(remainCartList);
            //删除用户购物车未选中订单
//                redisTemplate.delete("cart_" + order.getUserId() + "_remain");
//            } else {
            // 删除该用户购物车数据
//                redisTemplate.delete("cart_" + order.getUserId());
//            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 微信支付方式
     *
     * @param order       订单
     * @param orderIdList 订单id集合
     * @param totalMoney  订单总金额
     */
    private void payTypeIsWX(Order order, List<String> orderIdList, double totalMoney) {
        if ("1".equals(order.getPaymentType())) {
            //创建支付日志对象
            PayLog payLog = new PayLog();
            //生成订单交易号
            String outTradeNo = String.valueOf(idWorker.nextId());
            //设置订单交易号
            payLog.setOutTradeNo(outTradeNo);
            //创建时间
            payLog.setCreateTime(new Date());
            //支付总金额(分)
            payLog.setTotalFee((long) (totalMoney * 100));
            //用户ID
            payLog.setUserId(order.getUserId());
            //支付状态
            payLog.setTradeState("0");
            //订单号集合，逗号分隔
            String ids = orderIdList.toString().replace("[", "")
                    .replace("]", "").replace(" ", "");
            //设置订单号
            payLog.setOrderList(ids);
            //支付类型
            payLog.setPayType("1");
            //往支付日志表插入数据
            payLogMapper.insertSelective(payLog);
            //存入缓存
            redisTemplate.boundValueOps("payLog_" + order.getUserId()).set(payLog);
        }
    }

    /**
     * 保存数据到订单表和订单明细表中
     *
     * @param order       订单
     * @param carts       购物车
     * @param orderIdList 订单id集合
     * @return 订单总金额
     */
    private double insertDataAndGetTotalMoney(Order order, List<Cart> carts, List<String> orderIdList) {
        //定义多个订单支付的总金额（元）
        double totalMoney = 0;
        for (Cart cart : carts) {
            /* ####### 往订单表插入数据 ######### */
            // 生成订单主键id
            long orderId = idWorker.nextId();
            // 创建新的订单
            Order orderRow = new Order();
            orderRow.setOrderId(orderId);
            // 设置支付类型
            orderRow.setPaymentType(order.getPaymentType());
            // 设置支付状态码为“未支付”
            orderRow.setStatus("1");
            orderRow.setCreateTime(new Date());
            orderRow.setUpdateTime(orderRow.getCreateTime());
            orderRow.setUserId(order.getUserId());
            orderRow.setReceiverAreaName(order.getReceiverAreaName());
            // 设置收件人地址
            orderRow.setReceiverMobile(order.getReceiverMobile());
            orderRow.setReceiver(order.getReceiver());
            orderRow.setSourceType(order.getSourceType());
            orderRow.setSellerId(cart.getSellerId());

            // 定义该订单总金额
            double money = 0;
            /* ####### 往订单明细表插入数据(当前商家下的子订单) ######### */
            for (OrderItem orderItem : cart.getOrderItems()) {
                // 设置主键id
                orderItem.setId(idWorker.nextId());
                // 设置关联的订单id
                orderItem.setOrderId(orderId);
                // 累计该商家总金额
                money += orderItem.getTotalFee().doubleValue();
                // 保存数据到订单明细表
                orderItemMapper.insertSelective(orderItem);
            }
            // 设置支付总金额(一个商家的订单总金额)
            orderRow.setPayment(new BigDecimal(money));
            // 保存数据到订单表
            orderMapper.insertSelective(orderRow);

            //记录订单id
            orderIdList.add(String.valueOf(orderId));
            //记录所有商家总金额（付款总金额）
            totalMoney += money;
        }
        return totalMoney;
    }

    /**
     * 根据用户查询支付日志
     *
     * @param userId 用户id(用户名)
     * @return 支付日志
     */
    @Override
    public PayLog findPayLogFromRedis(String userId) {
        try {
            return (PayLog) redisTemplate
                    .boundValueOps("payLog_" + userId).get();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 修改订单状态
     *
     * @param outTradeNo    订单交易号
     * @param transactionId 微信交易流水号
     */
    @Override
    public void updateOrderStatus(String outTradeNo, String transactionId) {
        /* #########修改支付日志状态########## */
        PayLog payLog = payLogMapper.selectByPrimaryKey(outTradeNo);
        payLog.setPayTime(new Date());
        // 已支付
        payLog.setTradeState("1");
        // 交易流水号
        payLog.setTransactionId(transactionId);
        payLogMapper.updateByPrimaryKeySelective(payLog);

        /* #######修改订单状态########## */
        // 订单号列表
        String[] orderIds = payLog.getOrderList().split(",");
        // 循环订单号数组
        for (String orderId : orderIds) {
            Order order = new Order();
            order.setOrderId(Long.valueOf(orderId));
            // 支付时间
            order.setPaymentTime(new Date());
            // 已付款
            order.setStatus("2");
            orderMapper.updateByPrimaryKeySelective(order);
        }
        // 清除redis缓存数据
        redisTemplate.delete("payLog_" + payLog.getUserId());
    }

    /**
     * 交易关闭
     *
     * @param orderId 订单id
     * @return 关闭结果
     */
    @Override
    public boolean closeOrder(String orderId) {
        try {
            Order order = new Order();
            order.setOrderId(Long.valueOf(orderId));
            order.setStatus("6");
            int row = orderMapper.updateByPrimaryKeySelective(order);
            return row > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量保存订单
     *
     * @param params 参数
     */
    @Override
    public void saveMyBatchOrder(HashMap<String, Object> params) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        String[] orderIds = (String[]) params.get("ids");
        criteria.andIn("orderId", Arrays.asList(orderIds));
        List<Order> orders = orderMapper.selectByExample(example);
        double paymentTotal = 0.00;
        for (Order order : orders) {
            paymentTotal += order.getPayment().doubleValue();
        }
        //创建支付日志对象
        PayLog payLog = new PayLog();
        //生成订单交易号
        String outTradeNo = String.valueOf(idWorker.nextId());
        //设置订单交易号
        payLog.setOutTradeNo(outTradeNo);
        //创建时间
        payLog.setCreateTime(new Date());
        //支付总金额(分)
        payLog.setTotalFee((long) (paymentTotal * 100));
        //用户ID
        Object userId = params.get("userId");
        payLog.setUserId((String) userId);
        //支付状态
        payLog.setTradeState("0");
        //订单号集合，逗号分隔
        String ids = orderIds.toString().replace("[", "")
                .replace("]", "").replace(" ", "");
        //设置订单号
        payLog.setOrderList(ids);
        //支付类型
        payLog.setPayType("1");
        //往支付日志表插入数据
        payLogMapper.insertSelective(payLog);
        //存入缓存
        redisTemplate.boundValueOps("payLog_" + userId).set(payLog);
    }
}
