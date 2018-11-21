package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.Seller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference(timeout = 10000)
    private SellerService sellerService;
    /**
     * 注入身份认证管理器
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 添加商家
     */
    @PostMapping("/save")
    public boolean save(@RequestBody Seller seller) {
        try {
            // 密码加密
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            seller.setPassword(bCryptPasswordEncoder.encode(seller.getPassword()));

            sellerService.save(seller);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 查询该商家的详细信息
     *
     * @return 商家数据
     */
    @GetMapping("/showSellerInfo")
    public Seller showSellerInfo() {
        // 获取登录用户名
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        return sellerService.findOne(loginName);
    }

    /**
     * 更新该商家
     *
     * @return 更新结果
     */
    @PostMapping("/updateSellerInfo")
    public boolean updateSellerInfo(@RequestBody Seller seller) {
        return sellerService.updateSellerInfo(seller);
    }

    /**
     * 修改商家密码
     *
     * @param sellerPwd 商家密码
     * @return 修改结果
     */
    @PostMapping("/updateSellerPwd")
    public Map<String, String> updateSellerPwd(@RequestBody Map<String, String> sellerPwd) {
        //定义结果数据
        Map<String, String> resultMap = new HashMap<>(1);
        Seller seller = new Seller();
        //密码管理工具
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        try {
            // 获取登录用户名
            String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
            seller.setSellerId(loginName);
            seller.setPassword(sellerPwd.get("newPwd"));

            Seller findSeller = sellerService.findOne(loginName);
            if (bCryptPasswordEncoder.matches(sellerPwd.get("password"), findSeller.getPassword())) {
                // 密码加密
                seller.setPassword(bCryptPasswordEncoder.encode(seller.getPassword()));
                resultMap = sellerService.updatePassword(seller);
            } else {
                resultMap.put("PwdFault", "true");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

}