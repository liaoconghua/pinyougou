package com.pinyougou.sellergoods.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.common.pojo.Seller;

import java.util.Map;

public interface SellerService {
    /**
     * 添加商家
     */
    public void save(Seller seller);

    /**
     * 根据条件查询一页数据
     */
    PageResult findByPage(Seller seller, Integer page, Integer rows);

    /**
     * 审核商家(修改商家状态)
     */
    boolean updateStatus(String sellerId, String status);

    /**
     * 通过用户名查询商家
     *
     * @param username 用户名
     * @return 返回商家
     */
    Seller findOne(String username);

    /**
     * 更新该商家的详细信息
     *
     * @param seller 需修改的商家
     * @return 修改结果
     */
    boolean updateSellerInfo(Seller seller);

    /**
     * 更新商家密码
     *
     * @param seller 商家
     * @return 结果集
     */
    Map<String, String> updatePassword(Seller seller);
}
