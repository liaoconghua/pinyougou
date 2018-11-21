package com.pinyougou.service;

import com.pinyougou.common.pojo.ItemCat;
import com.pinyougou.common.pojo.PageResult;

import java.util.List;

public interface ItemCatService {
    /**
     * 分页查询数据
     */
    PageResult findByPage(Integer pageNum, Integer pageSize, Long parentId, ItemCat searchEntity);

    /**
     * 根据父级id查询商品分类
     */
    List<ItemCat> findItemCatByParentId(Long parentId);

    /**
     * 保存商品分类
     */
    boolean save(ItemCat itemCat);

    /**
     * 修改商品分类
     */
    boolean update(ItemCat itemCat);

    /**
     * 删除商品分类
     */
    boolean deleteByIds(Long[] ids);
}
