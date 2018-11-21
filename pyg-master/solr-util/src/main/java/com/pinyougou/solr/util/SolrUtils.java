package com.pinyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.Item;
import com.pinyougou.sellergoods.mapper.ItemMapper;
import com.pinyougou.solr.SolrItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtils {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 导入商品数据
     */
    public void importItemData() {
        //创建Item对象封装查询条件
        Item item = new Item();
        //正常的商品
        item.setStatus("1");
        //从数据库表中查询商品数据
        List<Item> itemList = itemMapper.select(item);
        System.out.println(itemList.size());
        System.out.println("===商品列表===");
        List<SolrItem> solrItems = new ArrayList<>();
        for (Item item1 : itemList) {
            SolrItem solrItem = new SolrItem();
            solrItem.setId(item1.getId());
            solrItem.setBrand(item1.getBrand());
            solrItem.setCategory(item1.getCategory());
            solrItem.setGoodsId(item1.getGoodsId());
            solrItem.setImage(item1.getImage());
            solrItem.setPrice(item1.getPrice());
            solrItem.setSeller(item1.getSeller());
            solrItem.setTitle(item1.getTitle());
            solrItem.setUpdateTime(item1.getUpdateTime());
            //将spec字段的json字符串转换成map
            Map<String, String> specMap = JSON.parseObject(item1.getSpec(), Map.class);
            //设置动态域
            solrItem.setSpecMap(specMap);
            solrItems.add(solrItem);
        }
        //保存数据到索引库
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
        if (updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        } else {
            solrTemplate.rollback();
        }
        System.out.println("===结束===");
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:applicationContext.xml");
        SolrUtils solrUtils = context.getBean(SolrUtils.class);
        solrUtils.importItemData();

    }

}
