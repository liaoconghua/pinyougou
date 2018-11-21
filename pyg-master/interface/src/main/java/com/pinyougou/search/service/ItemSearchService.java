package com.pinyougou.search.service;

import com.pinyougou.solr.SolrItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 搜索方法
     */
    Map<String, Object> search(Map<String, Object> params);

    /**
     * 添加或修改商品索引
     *
     * @param solrItems \封装后的商品sku的item集合
     */
    void saveOrUpdate(List<SolrItem> solrItems);

    /**
     * 删除商品索引
     *
     * @param idList 商品id集合
     */
    void delete(List<Long> idList);
}
