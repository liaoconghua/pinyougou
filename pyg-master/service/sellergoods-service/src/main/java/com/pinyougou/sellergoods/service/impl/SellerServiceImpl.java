package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.common.pojo.Seller;
import com.pinyougou.sellergoods.mapper.SellerMapper;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Transactional
@Service(interfaceName = "com.pinyougou.sellergoods.service.SellerService")
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SellerMapper sellerMapper;

    /**
     * 添加商家
     */
    @Override
    public void save(Seller seller) {
        try {
            seller.setStatus("0");
            seller.setCreateTime(new Date());
            sellerMapper.insertSelective(seller);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据条件查询一页数据
     */
    @Override
    public PageResult findByPage(Seller seller, Integer page, Integer rows) {
        try {
            Example example = new Example(Seller.class);
            Example.Criteria criteria = example.createCriteria();
            if (StringUtil.isNotEmpty(seller.getName())) {
                criteria.andLike("name", "%" + seller.getName() + "%");
            }
            if (StringUtil.isNotEmpty(seller.getNickName())) {
                criteria.andLike("nickName", "%" + seller.getNickName() + "%");
            }
            if (StringUtil.isNotEmpty(seller.getStatus())) {
                criteria.andEqualTo("status", seller.getStatus());
            }

            // 开始分页
            PageInfo<Seller> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(() -> sellerMapper.selectByExample(example));
            return new PageResult(pageInfo.getList(), pageInfo.getTotal());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 审核商家(修改商家状态)
     */
    @Override
    public boolean updateStatus(String sellerId, String status) {
        try {
            Seller seller = new Seller();
            seller.setSellerId(sellerId);
            seller.setStatus(status);
            int row = sellerMapper.updateByPrimaryKeySelective(seller);
            return row > 0;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 通过用户名查询商家
     */
    @Override
    public Seller findOne(String username) {
        return sellerMapper.selectByPrimaryKey(username);
    }

    /**
     * 更新该商家的详细信息
     *
     * @param seller 需修改的商家
     * @return 修改结果
     */
    @Override
    public boolean updateSellerInfo(Seller seller) {
        int row = sellerMapper.updateByPrimaryKeySelective(seller);
        return row > 0;
    }

    /**
     * 更新商家密码
     *
     * @param seller    商家
     * @return 结果集
     */
    @Override
    public Map<String, String> updatePassword(Seller seller) {
        try {
            //定义结果集
            HashMap<String, String> resultMap = new HashMap<>(1);
            int row = sellerMapper.updateByPrimaryKeySelective(seller);
            resultMap.put("updateRow", row + "");
            return resultMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
