package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.Content;
import com.pinyougou.content.service.ContentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference(timeout = 10000)
    private ContentService contentService;

    /**
     * 根据广告分类ID查询广告数据
     */
    @GetMapping("/findContentByCategoryId")
    public List<Content> findContentByCategoryId(Long categoryId) {
        return contentService.findContentByCategoryId(categoryId);
    }

}
