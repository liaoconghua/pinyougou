package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.search.service.ItemSearchService")
@Transactional
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 搜索方法
     */
    @Override
    public Map<String, Object> search(Map<String, Object> params) {
        //创建Map集合封装返回数据
        Map<String, Object> data = new HashMap<>();

        //获取检索关键字
        String keywords = (String) params.get("keywords");

        // 获取当前页码
        Integer page = (Integer) params.get("page");
        if (page == null) {
            page = 1;
        }
        //获取每页显示的记录数
        Integer rows = (Integer) params.get("rows");
        if (rows == null) {
            rows = 20;
        }

        //判断检索关键字
        if (StringUtils.isNoneBlank(keywords)) {
            //创建查询对象
            HighlightQuery highlightQuery = new SimpleHighlightQuery();
            //创建高亮选项对象
            HighlightOptions highlightOptions = new HighlightOptions();
            //对title进行高亮显示
            highlightOptions.addField("title");
            highlightOptions.setSimplePrefix("<font color='red'>");
            highlightOptions.setSimplePostfix("</font>");
            //设置高亮选项
            highlightQuery.setHighlightOptions(highlightOptions);

            //创建查询条件
            createSearchCriteria(params, keywords, highlightQuery);

            //设置起始记录查询数
            highlightQuery.setOffset((page - 1) * rows);
            //设置每页显示记录数
            highlightQuery.setRows((rows));

            //分页检索
            HighlightPage<SolrItem> highlightPage = createHighlightPage(highlightQuery);

            //获取内容
            data.put("rows", highlightPage.getContent());
            //设置总页数
            data.put("totalPages", highlightPage.getTotalPages());
            //设置总记录数
            data.put("total", highlightPage.getTotalElements());
        } else { // 无检索关键字，创建简单查询
            SimpleQuery simpleQuery = new SimpleQuery("*:*");
            //设置起始记录查询数
            simpleQuery.setOffset((page - 1) * rows);
            // 设置每页显示记录数
            simpleQuery.setRows(rows);

            ScoredPage scoredPage =
                    solrTemplate.queryForPage(simpleQuery, SolrItem.class);
            data.put("rows", scoredPage.getContent());
            //设置总页数
            data.put("totalPages", scoredPage.getTotalPages());
            // 设置总记录数
            data.put("total", scoredPage.getTotalElements());
        }
        return data;
    }

    /**
     * 高亮分页
     *
     * @param highlightQuery 高亮查询条件封装对象
     * @return 高亮分页数据
     */
    private HighlightPage<SolrItem> createHighlightPage(HighlightQuery highlightQuery) {
        HighlightPage<SolrItem> highlightPage = solrTemplate.queryForHighlightPage(highlightQuery, SolrItem.class);
        //循环高亮项集合
        for (HighlightEntry<SolrItem> highlightEntry : highlightPage.getHighlighted()) {
            //获取检索到的原实体
            SolrItem solrItem = highlightEntry.getEntity();
            //判断高亮集合及集合中第一个Field的高亮内容
            if (highlightEntry.getHighlights().size() > 0 &&
                    highlightEntry.getHighlights().get(0).getSnipplets().size() > 0) {
                //设置高亮的结果
                solrItem.setTitle(highlightEntry.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        return highlightPage;
    }

    /**
     * 创建过滤搜索条件
     *
     * @param params         过滤查询参数
     * @param keywords       查询搜索词
     * @param highlightQuery 高亮查询条件对象
     */
    private void createSearchCriteria(Map<String, Object> params, String keywords, HighlightQuery highlightQuery) {
        Criteria criteria = new Criteria("keywords").is(keywords);
        // 添加查询条件
        highlightQuery.addCriteria(criteria);

        // 按商品分类过滤
        if (!"".equals(params.get("category"))) {
            Criteria criteria1 = new Criteria("category")
                    .is(params.get("category"));
            // 添加过滤条件
            highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
        }
        // 按品牌过滤
        if (!"".equals(params.get("brand"))) {
            Criteria criteria1 = new Criteria("brand")
                    .is(params.get("brand"));
            // 添加过滤条件
            highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
        }

        //按规格过滤
        if (params.get("spec") != null) {
            Map<String, String> spec = (Map<String, String>) params.get("spec");
            for (String key : spec.keySet()) {
                Criteria criteria1 = new Criteria("spec_" + key).is(params.get(key));
                highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
            }
        }
        //按价格过滤
        if (!"".equals(params.get("price"))) {
            String[] prices = params.get("price").toString().split("-");
            //价格起点不为零
            if (!"0".equals(prices[0])) {
                Criteria criteria1 = new Criteria("price").greaterThanEqual(prices[0]);
                highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
            }
            //终点不为*
            if (!"*".equals(prices[1])) {
                Criteria criteria1 = new Criteria("price").lessThanEqual(prices[1]);
                highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
            }
        }
        // 添加排序
        createSearchSort(params, highlightQuery);

    }

    private void createSearchSort(Map<String, Object> params, HighlightQuery highlightQuery) {
        //排序域
        String sortField = (String) params.get("sortField");
        //排序方式 升序ASC  降序DESC
        String sort = (String) params.get("sort");
        if (StringUtils.isNoneBlank(sortField) && StringUtils.isNoneBlank(sort)) {
            Sort sortDetails = new Sort("ASC".equalsIgnoreCase(sort) ?
                    Sort.Direction.ASC : Sort.Direction.DESC, sortField);
            //添加排序到查询条件中
            highlightQuery.addSort(sortDetails);
        }
    }

    /**
     * 添加或修改商品索引
     *
     * @param solrItems 封装后的商品sku的item集合
     */
    @Override
    public void saveOrUpdate(List<SolrItem> solrItems) {
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
        if (updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        } else {
            solrTemplate.rollback();
        }
    }

    /**
     * 删除商品索引
     *
     * @param idList 商品id集合
     */
    @Override
    public void delete(List<Long> idList) {
        SimpleQuery simpleQuery = new SimpleQuery();
        Criteria goodsIdCriteria = new Criteria("goodsId").in(idList);
        simpleQuery.addCriteria(goodsIdCriteria);
        UpdateResponse updateResponse = solrTemplate.delete(simpleQuery);
        if (updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        } else {
            solrTemplate.rollback();
        }
    }
}
