package com.pinyougou.sellergoods.mapper;

import com.pinyougou.common.pojo.Goods;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface GoodsMapper extends Mapper<Goods> {
    /**
     * 按条件查询所有商品
     */
    List<Map<String, Object>> findAll(Goods goods);
}
