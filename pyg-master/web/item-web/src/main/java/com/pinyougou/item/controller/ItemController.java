package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;

@Controller
public class ItemController {
    @Reference(timeout = 10000)
    private GoodsService goodsService;

    /**
     * 根据id查询商品信息
     */
    @GetMapping("/{goodsId}")
    public String getGoods(@PathVariable Long goodsId, Model model) {
        HashMap<String, Object> data = goodsService.getGoods(goodsId);
        model.addAllAttributes(data);
        return "item";
    }

}
