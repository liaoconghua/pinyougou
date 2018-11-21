package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.Cart;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.pojo.OrderItem;
import com.pinyougou.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车控制器
 *
 * @author Fa
 * @date 2018-10-26 18:35
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 300000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /**
     * 添加SKU商品到购物车
     */
    @GetMapping("/addCart")
    @CrossOrigin(origins = "http://item.pinyougou.com",
            allowCredentials = "true")
    public boolean addCart(Long itemId, Integer num) {
        try {
            //获取登录用户名
            String username = request.getRemoteUser();
            //获取购物车集合
            List<Cart> carts = findCart();
            // 调用服务层添加SKU商品到购物车
            carts = cartService.addItemToCart(carts, itemId, num);
            if (StringUtils.isNoneBlank(username)) {
                //######## 往Redis存储购物车 #######
                cartService.saveCartRedis(username, carts);
            } else {
                // 将购物车重新存入Cookie中
                CookieUtils.setCookie(request, response,
                        CookieUtils.CookieName.PINYOUGOU_CART,
                        JSON.toJSONString(carts),
                        3600 * 24, true);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取购物车集合
     */
    @GetMapping("/findCart")
    private List<Cart> findCart() {
        try {
            // 获取登录用户名
            String username = request.getRemoteUser();
            // 定义购物车集合
            List<Cart> carts = null;
            // 判断用户是否登录
            if (StringUtils.isNoneBlank(username)) {
                // ######## 从Redis获取购物车 #######
                carts = cartService.findCartRedis(username);
                //从Cookie中获取购物车集合json字符串
                String cartsStr = CookieUtils.getCookieValue(
                        request, CookieUtils.CookieName.PINYOUGOU_CART, true);
                //判断是否为空
                if (StringUtils.isNoneBlank(cartsStr)) {
                    List<Cart> cookieCarts = JSON.parseArray(cartsStr, Cart.class);
                    if (cookieCarts != null && cookieCarts.size() > 0) {
                        // 合并购物车
                        carts = cartService.mergeCart(cookieCarts, carts);
                        // 将合并后的购物车存入Redis
                        cartService.saveCartRedis(username, carts);
                        // 删除Cookie购物车
                        CookieUtils.deleteCookie(request, response,
                                CookieUtils.CookieName.PINYOUGOU_CART);
                    }
                }
            } else {
                //######## 从Cookie获取购物车 #######
                String cartStr = CookieUtils.getCookieValue(
                        request, CookieUtils.CookieName.PINYOUGOU_CART, true);
                if (StringUtils.isBlank(cartStr)) {
                    cartStr = "[]";
                }
                carts = JSON.parseArray(cartStr, Cart.class);
            }
            return carts;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过选中itemId获取购物车集合
     */
    @PostMapping("/findCartByItemId")
    private List<Cart> findCartByItemId(@RequestBody List<Long[]> itemIds) {
        try {
            // 获取登录用户名
            String username = request.getRemoteUser();
            // 定义购物车集合
            List<Cart> carts;
            List<Cart> cartList = new ArrayList<>();
            // 判断用户是否登录
            if (StringUtils.isNoneBlank(username)) {
                // ######## 从Redis获取购物车 #######
                carts = cartService.findCartRedis(username);
                //从Cookie中获取购物车集合json字符串
                String cartsStr = CookieUtils.getCookieValue(
                        request, CookieUtils.CookieName.PINYOUGOU_CART, true);
                //判断是否为空
                if (StringUtils.isNoneBlank(cartsStr)) {
                    List<Cart> cookieCarts = JSON.parseArray(cartsStr, Cart.class);
                    if (cookieCarts != null && cookieCarts.size() > 0) {
                        // 合并购物车
                        carts = cartService.mergeCart(cookieCarts, carts);
                        // 将合并后的购物车存入Redis
                        cartService.saveCartRedis(username, carts);
                        // 删除Cookie购物车
                        CookieUtils.deleteCookie(request, response,
                                CookieUtils.CookieName.PINYOUGOU_CART);
                    }
                }
            } else {
                //######## 从Cookie获取购物车 #######
                String cartStr = CookieUtils.getCookieValue(
                        request, CookieUtils.CookieName.PINYOUGOU_CART, true);
                if (StringUtils.isBlank(cartStr)) {
                    cartStr = "[]";
                }
                carts = JSON.parseArray(cartStr, Cart.class);
            }
            //将合并后的购物车中的需结算的订单取出来
            cartList = getMyCarts(itemIds, carts, username);
            return cartList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 需结算的订单
     *
     * @param itemIds  需结算的sku订单id集合
     * @param carts    用户购物车中的所有订单集合
     * @param username 用户名
     * @return 需结算的订单集合
     */
    private List<Cart> getMyCarts(List<Long[]> itemIds, List<Cart> carts, String username) {
        List<Cart> cartList = new ArrayList<>();
        List<Cart> remainCartList = new ArrayList<>();
        for (Cart cart : carts) {
            Cart myCart = new Cart();
            myCart.setSellerId(cart.getSellerId());
            myCart.setSellerName(cart.getSellerName());
            Cart remainMyCart = new Cart();
            remainMyCart.setSellerId(cart.getSellerId());
            remainMyCart.setSellerName(cart.getSellerName());
            List<OrderItem> orderItemList = new ArrayList<>();
            List<OrderItem> remainOrderItemList = new ArrayList<>();
            List<OrderItem> orderItems = cart.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                for (Long[] itemId : itemIds) {
                    for (Long id : itemId) {
                        if (orderItem.getItemId().equals(id)) {
                            orderItemList.add(orderItem);
                        } else {
                            if (remainOrderItemList.size() > 0) {
                                boolean flag = true;
                                for (OrderItem remainOrderItem : remainOrderItemList) {
                                    if (remainOrderItem.getItemId().equals(orderItem.getItemId())) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    remainOrderItemList.add(orderItem);
                                }
                            } else {
                                remainOrderItemList.add(orderItem);
                            }
                        }
                    }
                }
            }
            for (OrderItem item : orderItemList) {
                for (OrderItem orderItem : remainOrderItemList) {
                    if (item.getItemId().equals(orderItem.getItemId())) {
                        remainOrderItemList.remove(orderItem);
                        break;
                    }
                }
            }
            myCart.setOrderItems(orderItemList);
            remainMyCart.setOrderItems(remainOrderItemList);
            if (orderItemList.size() > 0) {
                cartList.add(myCart);
            }
            if (remainOrderItemList.size() > 0) {
                boolean flag = true;
                for (Cart remainCart : remainCartList) {
                    if (remainCart.getSellerId().equals(remainMyCart.getSellerId())) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    remainCartList.add(remainMyCart);
                }
            }
        }
        if (remainCartList.size() > 0) {
            // 将剩下后的购物车存入Redis
            cartService.saveCartRedis(username + "_remain", remainCartList);
        }
        //将选中的订单存入Redis中
        cartService.saveCartRedis(username + "_selected", cartList);
        return cartList;
    }

    private List<Cart> getMyCarts1(@RequestBody List<Long[]> itemIds, List<Cart> carts, String username) {
        List<Cart> cartList = new ArrayList<>();
        List<Cart> remainCartList = new ArrayList<>();
        for (int j = 0; j < itemIds.size(); j++) {
            Cart cart = carts.get(j);
            Cart myCart = new Cart();
            myCart.setSellerId(cart.getSellerId());
            myCart.setSellerName(cart.getSellerName());
            Cart remainMyCart = new Cart();
            remainMyCart.setSellerId(cart.getSellerId());
            remainMyCart.setSellerName(cart.getSellerName());
            List<OrderItem> orderItemList = new ArrayList<>();
            List<OrderItem> remainOrderItemList = new ArrayList<>();
            for (OrderItem orderItem : carts.get(j).getOrderItems()) {
                for (Long id : itemIds.get(j)) {
                    if (orderItem.getItemId().equals(id)) {
                        orderItemList.add(orderItem);
                    } else {
                        remainOrderItemList.add(orderItem);
                    }
                }
            }
            myCart.setOrderItems(orderItemList);
            remainMyCart.setOrderItems(remainOrderItemList);
            if (orderItemList.size() > 0) {
                cartList.add(myCart);
            } else {
                remainCartList.add(carts.get(j));
            }
            if (remainOrderItemList.size() > 0) {
                remainCartList.add(remainMyCart);
            }
        }
        if (remainCartList.size() > 0) {
            // 将剩下后的购物车存入Redis
            cartService.saveCartRedis(username + "_remain", remainCartList);
        }
        //将选中的订单存入Redis中
        cartService.saveCartRedis(username + "_selected", cartList);
        return cartList;
    }

    /**
     * 删除购物车中的所有订单数据
     *
     * @param request 请求对象
     * @return 删除结果
     */
    @GetMapping("/deleteCarts")
    public boolean deleteCarts(HttpServletRequest request) {
        try {
            String username = request.getRemoteUser();
            return cartService.deleteCarts(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
