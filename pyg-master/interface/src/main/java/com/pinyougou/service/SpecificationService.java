package com.pinyougou.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.common.pojo.Specification;
import com.pinyougou.common.pojo.SpecificationOption;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    /**
     * 查询一页数据
     */
    PageResult findByPage(Integer pageNum, Integer pageSize, Specification searchEntity);

    /**
     * 保存规格
     */
    boolean save(Specification specification);

    /**
     * 根据规格id查询更新数据回显
     */
    List<SpecificationOption> findSpecification(Long id);

    /**
     * 更新规格
     */
    boolean update(Specification specification);

    /**
     * 删除规格
     */
    boolean deleteByIds(Long[] ids);

    /**
     * 查询所有的规格(id与specName)
     */
    List<Map<String, Object>> findAllByIdAndName();
}
