package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.common.pojo.Seller;
import com.pinyougou.sellergoods.service.SellerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.util.StringUtil;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference(timeout = 10000)
    private SellerService sellerService;

    /**
     * 多条件分页查询商家
     */
    @GetMapping("/findByPage")
    public PageResult findByPage(Seller seller, Integer pageNum, Integer pageSize) {
        try {
            // GET请求中文转码
            if (seller != null && StringUtils.isNoneBlank(seller.getName())) {
                seller.setName(new String(seller.getName()
                        .getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            }
            if (seller != null && StringUtils.isNoneBlank(seller.getNickName())) {
                seller.setNickName(new String(seller.getNickName()
                        .getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sellerService.findByPage(seller, pageNum, pageSize);
    }

    /**
     * 审核商家(修改商家状态)
     */
    @GetMapping("/updateStatus")
    public boolean updateStatus(String sellerId, String status) {
        try {
            if (StringUtil.isNotEmpty(sellerId)) {
                sellerId = new String(sellerId.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            }
            return sellerService.updateStatus(sellerId, status);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}