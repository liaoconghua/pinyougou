package com.pinyougou.content.service;

import com.pinyougou.common.pojo.Content;
import com.pinyougou.common.pojo.ContentCategory;
import com.pinyougou.common.pojo.PageResult;

import java.util.List;

public interface ContentCategoryService {
    /**
     * 多条件查询方法
     */
    PageResult findByPage(Content content, Integer pageNum, Integer pageSize);

    /**
     * 加载广告分类数据
     */
    List<ContentCategory> findAll();

    /**
     * 保存内容分类数据
     */
    boolean save(ContentCategory contentCategory);

    /**
     * 修改内容分类数据
     */
    boolean update(ContentCategory contentCategory);

    /**
     * 通过id批量删除内容分类
     */
    boolean deleteByIds(Long[] ids);
}
