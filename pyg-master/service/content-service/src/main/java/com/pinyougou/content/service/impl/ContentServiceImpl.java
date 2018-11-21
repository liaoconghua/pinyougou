package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.Content;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.content.mapper.ContentMapper;
import com.pinyougou.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

@Transactional
@Service(interfaceName = "com.pinyougou.content.service.ContentService")
public class ContentServiceImpl implements ContentService {
    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private RedisTemplate<String, List<Content>> redisTemplate;

    /**
     * 多条件查询方法
     */
    @Override
    public PageResult findByPage(Content content, Integer pageNum, Integer pageSize) {

        PageInfo<Object> pageInfo = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(
                () -> contentMapper.selectAll());
        return new PageResult(pageInfo.getList(), pageInfo.getTotal());
    }

    /**
     * 保存内容
     */
    @Override
    public boolean save(Content content) {
        int row;
        try {
            row = contentMapper.insertSelective(content);
            //清除redis缓存
            redisTemplate.delete("content");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return row > 0;
    }

    /**
     * 修改内容
     */
    @Override
    public boolean update(Content content) {
        int row;
        try {
            row = contentMapper.updateByPrimaryKeySelective(content);
            //清除redis缓存
            redisTemplate.delete("content");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return row > 0;
    }

    /**
     * 通过id批量删除内容
     */
    @Override
    public boolean deleteByIds(Long[] ids) {
        int row = 0;
        try {
            Example contentExample = new Example(Content.class);
            Example.Criteria criteria = contentExample.createCriteria();
            criteria.andIn("id", Arrays.asList(ids));

            row = contentMapper.deleteByExample(contentExample);
            //清除redis缓存
            redisTemplate.delete("content");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return row > 0;
    }

    /**
     * 根据广告分类ID查询广告数据
     */
    @Override
    public List<Content> findContentByCategoryId(Long categoryId) {
        // 定义广告数据
        List<Content> contentList;
        try {
            // 从Redis中获取广告
            contentList = redisTemplate.boundValueOps("content").get();
            if (contentList != null && contentList.size() > 0) {
                return contentList;
            }
        } catch (Exception ignored) {
        }
        try {
            // 创建示范对象
            Example example = new Example(Content.class);
            //创建查询条件对象
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("categoryId", categoryId);
            criteria.andEqualTo("status", "1");
            // 排序(升序) order by sort_order asc
            example.orderBy("sortOrder").asc();

            //查询广告数据
            contentList = contentMapper.selectByExample(example);
            try {
                //存入redis缓存
                redisTemplate.boundValueOps("content").set(contentList);
            } catch (Exception ignored) {
            }
            return contentList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
