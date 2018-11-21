package com.pinyougou.service;

import com.pinyougou.common.pojo.Brand;

import java.util.List;

public interface BrandService {
    /**
     * 查询所有品牌
     */
    public List<Brand> findAll(Integer pageNum, Integer pageSize);

    /**
     * 保存品牌
     */
    boolean save(Brand brand);

    /**
     * 修改品牌
     */
    boolean update(Brand brand);
}
