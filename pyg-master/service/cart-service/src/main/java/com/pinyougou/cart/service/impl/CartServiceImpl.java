package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.Cart;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.pojo.Item;
import com.pinyougou.common.pojo.OrderItem;
import com.pinyougou.sellergoods.mapper.ItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务接口实现类
 *
 * @author Fa
 * @date 2018-10-26 19:07
 */
@Transactional
@Service(interfaceName = "com.pinyougou.cart.service.CartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 添加sku到购物车
     *
     * @param carts  购物车（一个cart对应一个商家）原购物车
     * @param itemId sku商品id
     * @param num    购买数量
     * @return 修改后的购物车
     */
    @Override
    public List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num) {
        try {
            // 根据SKU商品ID查询SKU商品对象
            Item item = itemMapper.selectByPrimaryKey(itemId);
            //获取商家id
            String sellerId = item.getSellerId();
            // 根据商家id判断购物车集合中是否存在该商家的购物车
            Cart cart = searchCartBySellerId(carts, sellerId);

            List<OrderItem> orderItems = new ArrayList<>();
            //原购物车中无该商家
            if (cart == null) {
                cart = new Cart();
                cart.setSellerId(sellerId);
                cart.setSellerName(item.getSeller());

                OrderItem orderItem = createOrderItem(item, num);

                orderItems.add(orderItem);
                cart.setOrderItems(orderItems);

                carts.add(cart);
            } else {
                //有商家，先获取原订单集合
                orderItems = cart.getOrderItems();
                //再查询是否有相同的订单
                OrderItem orderItem = searchOrderItemByItemId(orderItems, itemId);
                //没有搜索到该订单
                if (orderItem == null) {
                    orderItem = createOrderItem(item, num);
                    cart.getOrderItems().add(orderItem);
                } else {
                    orderItem.setNum(orderItem.getNum() + num);
                    orderItem.setTotalFee(new BigDecimal(
                            item.getPrice().doubleValue() * orderItem.getNum()));
                }
                //判断num
                if (orderItem.getNum() <= 0) {
                    orderItems.remove(orderItem);
                }
                if (orderItems.size() <= 0) {
                    carts.remove(cart);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return carts;
    }

    /**
     * 通过itemId搜索订单
     *
     * @param orderItems 当前商家的订单集合
     * @param itemId     sku的id
     * @return 订单
     */
    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItems, Long itemId) {
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getItemId().equals(itemId)) {
                return orderItem;
            }
        }
        return null;
    }

    private OrderItem createOrderItem(Item item, Integer num) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        return orderItem;
    }

    /**
     * 搜索原购物车中是否有指定商家id的商家
     *
     * @param carts    原购物车
     * @param sellerId 需要搜索的商家id
     * @return 搜索到的商家
     */
    private Cart searchCartBySellerId(List<Cart> carts, String sellerId) {
        for (Cart cart : carts) {
            if (cart.getSellerId() != null && cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    /**
     * 从redis中获取购物车
     *
     * @param username 用户名
     * @return 购物车
     */
    @Override
    public List<Cart> findCartRedis(String username) {
        try {
            List<Cart> carts = (List<Cart>) redisTemplate.boundValueOps("cart_" + username).get();
            if (carts == null) {
                carts = new ArrayList<>(0);
            }
            return carts;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存购物车到redis中
     *
     * @param username 用户名
     * @param carts    购物车
     */
    @Override
    public void saveCartRedis(String username, List<Cart> carts) {
        redisTemplate.boundValueOps("cart_" + username).set(carts);
    }

    /**
     * 合并redis和cookie中的购物车
     *
     * @param cookieCarts 存储在cookie中的购物车
     * @param redisCarts  存储在redis中的购物车
     * @return 合并后的购物车
     */
    @Override
    public List<Cart> mergeCart(List<Cart> cookieCarts, List<Cart> redisCarts) {
        for (Cart cookieCart : cookieCarts) {
            for (OrderItem orderItem : cookieCart.getOrderItems()) {
                redisCarts = addItemToCart(redisCarts, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return redisCarts;
    }

    /**
     * 删除用户购物车在redis中的数据
     *
     * @param username 用户名
     * @return 删除结果
     */
    @Override
    public boolean deleteCarts(String username) {
        try {
            redisTemplate.delete("cart_" + username);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
