package com.pinyougou.sellergoods.service;

import com.pinyougou.common.pojo.Goods;
import com.pinyougou.common.pojo.Item;
import com.pinyougou.common.pojo.PageResult;

import java.util.HashMap;
import java.util.List;

public interface GoodsService {
    /**
     * 添加商品
     */
    boolean save(Goods goods);

    /**
     * 多条件分页查询商品
     *
     * @param goods 商品对象
     * @param page  当前页码
     * @param rows  每页显示的记录数
     * @return 商品分页数据
     */
    PageResult findByPage(Goods goods, Integer page, Integer rows);

    /**
     * 商品审批，修改商品状态
     */
    boolean updateStatus(Long[] ids, String status);

    /**
     * 商品删除，修改商品删除状态
     */
    boolean deleteAll(Long[] ids);

    /**
     * 商家商品上下架(修改可销售状态)
     */
    boolean updateMarketable(Long[] ids, String status);

    /**
     * 查询需修改的商品回显数据
     */
    Goods findGoodsById(Long id);

    /**
     * 修改商品
     */
    boolean update(Goods goods);

    /**
     * 根据id查询商品信息
     */
    HashMap<String, Object> getGoods(Long goodsId);

    /**
     * 根据商品id数组批量查询sku
     *
     * @param ids 商品id数组
     * @return 商品的sku集合
     */
    List<Item> findItemByGoodsId(Long[] ids);
}
