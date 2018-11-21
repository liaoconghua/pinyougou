package com.pinyougou.content.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.Content;
import com.pinyougou.common.pojo.ContentCategory;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.content.service.ContentCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {
    @Reference(timeout = 10000)
    private ContentCategoryService contentCategoryService;


    /**
     * 多条件查询方法
     */
    @GetMapping("/findByPage")
    public PageResult findByPage(Content content, Integer pageNum, Integer pageSize) {
        return contentCategoryService.findByPage(content, pageNum, pageSize);
    }

    /**
     * 加载广告分类数据
     */
    @GetMapping("/findAll")
    public List<ContentCategory> findAll() {
        return contentCategoryService.findAll();
    }

    /**
     * 保存内容分类数据
     */
    @PostMapping("/save")
    public boolean save(@RequestBody ContentCategory contentCategory) {
        return contentCategoryService.save(contentCategory);
    }

    /**
     * 修改内容分类
     */
    @PostMapping("/update")
    public boolean update(@RequestBody ContentCategory contentCategory) {
        return contentCategoryService.update(contentCategory);
    }

    /**
     * 通过id批量删除内容分类
     */
    @GetMapping("/deleteByIds")
    public boolean deleteByIds(Long[] ids) {
        return contentCategoryService.deleteByIds(ids);
    }
}
