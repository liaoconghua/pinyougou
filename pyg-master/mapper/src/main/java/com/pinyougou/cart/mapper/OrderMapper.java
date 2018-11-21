package com.pinyougou.cart.mapper;

import com.pinyougou.common.pojo.Order;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author Fa
 * @date 2018-10-28 19:54
 */
public interface OrderMapper extends Mapper<Order> {

    /**
     * 查询该用户下的所有订单
     *
     * @param params 用户名
     * @return 结果集
     */
    List<Map<String, Object>> findOrderByUsername(Map<String, Object> params);
}
