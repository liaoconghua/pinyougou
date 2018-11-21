package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.Goods;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.sellergoods.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference(timeout = 10000)
    private GoodsService goodsService;

    /**
     * 多条件分页查询商品
     */
    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer pageNum, Integer pageSize) {
        //添加查询条件
        goods.setAuditStatus("0");
        //GET请求中文转码
        if (StringUtils.isNoneBlank(goods.getGoodsName())) {
            goods.setGoodsName(new String(goods.getGoodsName().getBytes(
                    StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
        }
        //调用服务层方法查询
        return goodsService.findByPage(goods, pageNum, pageSize);
    }

    /**
     * 商品审批，修改商品状态
     */
    @GetMapping("/updateStatus")
    public boolean updateStatus(Long[] ids, String status) {
        return goodsService.updateStatus(ids, status);
    }

    /**
     * 商品删除，修改商品删除状态
     */
    @GetMapping("/deleteByIds")
    public boolean deleteByIds(Long[] ids) {
        return goodsService.deleteAll(ids);
    }

}
