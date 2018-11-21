package com.pinyougou.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.Brand;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceName = "com.pinyougou.service.BrandService")
@Transactional
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 查询所有品牌
     */
    @Override
    public List<Brand> findAll(Integer pageNum, Integer pageSize) {
        PageInfo<Brand> pageInfo = PageHelper.startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> brandMapper.selectAll());
        return pageInfo.getList();
    }

    /**
     * 保存品牌
     */
    @Override
    public boolean save(Brand brand) {
        int row;
        try {
            row = brandMapper.insertSelective(brand);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return row > 0;
    }

    /**
     * 修改品牌
     */
    @Override
    public boolean update(Brand brand) {
        int row;
        try {
            row = brandMapper.updateByPrimaryKeySelective(brand);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return row > 0;
    }
}
