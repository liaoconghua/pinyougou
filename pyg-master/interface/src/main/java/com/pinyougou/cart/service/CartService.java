package com.pinyougou.cart.service;

import com.pinyougou.cart.Cart;

import java.util.List;

/**
 * 购物车服务接口
 *
 * @author Fa
 * @date 2018-10-26 18:37
 */
public interface CartService {

    /**
     * 添加sku到购物车
     *
     * @param carts  购物车（一个cart对应一个商家）
     * @param itemId sku商品id
     * @param num    购买数量
     * @return 修改后的购物车
     */
    List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num);

    /**
     * 从redis中获取购物车
     *
     * @param username 用户名
     * @return 购物车
     */
    List<Cart> findCartRedis(String username);

    /**
     * 保存购物车到redis中
     *
     * @param username 用户名
     * @param carts    购物车
     */
    void saveCartRedis(String username, List<Cart> carts);

    /**
     * 合并redis和cookie中的购物车
     *
     * @param cookieCarts 存储在cookie中的购物车
     * @param redisCarts  存储在redis中的购物车
     * @return 合并后的购物车
     */
    List<Cart> mergeCart(List<Cart> cookieCarts, List<Cart> redisCarts);

    /**
     * 删除用户在redis中的购物车数据
     *
     * @param username 用户名
     * @return 删除结果
     */
    boolean deleteCarts(String username);
}
