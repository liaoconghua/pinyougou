package com.pinyougou.content.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.Content;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.content.service.ContentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference(timeout = 10000)
    private ContentService contentService;

    /**
     * 多条件查询方法
     */
    @GetMapping("/findByPage")
    public PageResult findByPage(Content content, Integer pageNum, Integer pageSize) {
        return contentService.findByPage(content, pageNum, pageSize);
    }

    /**
     * 保存内容
     */
    @PostMapping("/save")
    public boolean save(@RequestBody Content content) {
        return contentService.save(content);
    }

    /**
     * 修改内容
     */
    @PostMapping("/update")
    public boolean update(@RequestBody Content content) {
        return contentService.update(content);
    }

    /**
     * 通过id批量删除内容
     */
    @GetMapping("/deleteByIds")
    public boolean deleteByIds(Long[] ids) {
        return contentService.deleteByIds(ids);
    }
}
