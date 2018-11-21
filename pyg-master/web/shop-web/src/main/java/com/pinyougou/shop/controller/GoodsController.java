package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.Goods;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.sellergoods.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.jms.Destination;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination solrQueue;
    @Autowired
    private Destination solrDeleteQueue;
    @Autowired
    private Destination pageTopic;
    @Autowired
    private Destination pageDeleteTopic;


    /**
     * 添加商品
     */
    @PostMapping("/save")
    public boolean save(@RequestBody Goods goods) {
        try {
            //获取登录用户名
            String sellerId = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            //设置商家ID
            goods.setSellerId(sellerId);
            return goodsService.save(goods);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 多条件分页查询商品
     */
    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer page, Integer rows) {
        //获取登录商家编号
        String sellerId = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        //添加查询条件
        goods.setSellerId(sellerId);
        //GET请求中文转码
        if (StringUtils.isNoneBlank(goods.getGoodsName())) {
            goods.setGoodsName(new String(goods.getGoodsName().getBytes(
                    StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
        }
        //调用服务层方法查询
        return goodsService.findByPage(goods, page, rows);
    }

    /**
     * 商家商品上下架(修改可销售状态)
     */
    @GetMapping("/updateMarketable")
    public boolean updateMarketable(Long[] ids, String status) {
        boolean marketable = goodsService.updateMarketable(ids, status);
        if ("1".equals(status)) {
            //发送消息生成商品索引
            jmsTemplate.send(solrQueue, session -> session.createObjectMessage(ids));
            //发送消息生成静态网页
            for (Long id : ids) {
                jmsTemplate.send(pageTopic, session -> session.createTextMessage(id.toString()));
            }
        } else {//表示下架
            //发送消息生成商品索引
            jmsTemplate.send(solrDeleteQueue, session -> session.createObjectMessage(ids));
            //发送消息，删除静态页面
            jmsTemplate.send(pageDeleteTopic, session -> session.createObjectMessage(ids));
        }
        return marketable;
    }

    /**
     * 查询需修改的商品回显数据
     */
    @GetMapping("/findGoodsById")
    public Goods findGoodsAllById(Long id) {
        return goodsService.findGoodsById(id);
    }

    /**
     * 更新商品数据
     */
    @PostMapping("/update")
    public boolean update(@RequestBody Goods goods) {
        return goodsService.update(goods);
    }
}
