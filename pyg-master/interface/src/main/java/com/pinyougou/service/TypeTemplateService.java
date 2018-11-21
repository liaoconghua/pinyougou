package com.pinyougou.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.common.pojo.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {
    /**
     * 查询一页数据
     */
    PageResult findByPage(Integer pageNum, Integer pageSize, TypeTemplate searchEntity);

    /**
     * 添加类型模板
     */
    void save(TypeTemplate typeTemplate);

    /**
     * 修改类型模板
     */
    boolean update(TypeTemplate typeTemplate);

    /**
     * 删除类型模板
     */
    boolean deleteByIds(Long[] ids);

    /**
     * 查询所有类型模板
     */
    List<Map<String, Object>> findAllTypeTemplate();

    /**
     * 根据主键id查询类型模版
     */
    TypeTemplate findOne(Long id);

    /**
     * 根据模版id查询所有的规格与规格选项
     */
    List<Map> findSpecByTemplateId(Long id);
}
