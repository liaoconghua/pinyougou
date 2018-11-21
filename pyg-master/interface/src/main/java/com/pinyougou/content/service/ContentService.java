package com.pinyougou.content.service;

import com.pinyougou.common.pojo.Content;
import com.pinyougou.common.pojo.PageResult;

import java.util.List;

public interface ContentService {

    /**
     * 多条件查询方法
     */
    PageResult findByPage(Content content, Integer pageNum, Integer pageSize);

    /**
     * 保存内容
     */
    boolean save(Content content);

    /**
     * 修改内容
     */
    boolean update(Content content);

    /**
     * 通过id批量删除内容
     */
    boolean deleteByIds(Long[] ids);

    /**
     * 根据广告分类ID查询广告数据
     */
    List<Content> findContentByCategoryId(Long categoryId);
}
