package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.Content;
import com.pinyougou.common.pojo.ContentCategory;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.content.mapper.ContentCategoryMapper;
import com.pinyougou.content.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

@Transactional
@Service(interfaceName = "com.pinyougou.content.service.ContentCategoryService")
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private ContentCategoryMapper contentCategoryMapper;

    /**
     * 多条件查询方法
     */
    @Override
    public PageResult findByPage(Content content, Integer pageNum, Integer pageSize) {
        PageInfo<Object> pageInfo = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(
                () -> contentCategoryMapper.selectAll());
        return new PageResult(pageInfo.getList(), pageInfo.getTotal());
    }

    /**
     * 加载广告分类数据
     */
    @Override
    public List<ContentCategory> findAll() {
        return contentCategoryMapper.selectAll();
    }

    /**
     * 保存内容分类数据
     */
    @Override
    public boolean save(ContentCategory contentCategory) {
        int row = contentCategoryMapper.insertSelective(contentCategory);
        return row > 0;
    }

    /**
     * 修改内容分类数据
     */
    @Override
    public boolean update(ContentCategory contentCategory) {
        int row = contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
        return row > 0;
    }

    /**
     * 通过id批量删除内容分类
     */
    @Override
    public boolean deleteByIds(Long[] ids) {
        Example example = new Example(ContentCategory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));

        int row = contentCategoryMapper.deleteByExample(example);
        return row > 0;
    }
}
