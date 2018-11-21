package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.util.StringUtil;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.common.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {
    @Reference(timeout = 10000)
    private TypeTemplateService typeTemplateService;

    /**
     * 查询一页数据
     */
    @GetMapping("/findByPage")
    public PageResult findByPage(Integer pageNum, Integer pageSize, TypeTemplate searchEntity) {
        if (StringUtil.isEmpty(searchEntity.getName())) {
            searchEntity.setName("");
        }
        searchEntity.setName(new String(searchEntity.getName()
                .getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));

        return typeTemplateService.findByPage(pageNum, pageSize, searchEntity);
    }

    /**
     * 添加类型模板
     */
    @PostMapping("/save")
    public boolean save(@RequestBody TypeTemplate typeTemplate) {
        try {
            typeTemplateService.save(typeTemplate);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 修改类型模板
     */
    @PostMapping("/update")
    public boolean update(@RequestBody TypeTemplate typeTemplate) {
        return typeTemplateService.update(typeTemplate);
    }

    /**
     * 删除类型模板
     */
    @GetMapping("/delete")
    public boolean delete(Long[] ids) {
        return typeTemplateService.deleteByIds(ids);
    }

    /**
     * 查询所有类型模板
     */
    @GetMapping("/findAllTypeTemplate")
    public List<Map<String, Object>> findAllTypeTemplate() {
        return typeTemplateService.findAllTypeTemplate();
    }

    /**
     * 批量删除
     */
    @GetMapping("/deleteByIds")
    public boolean deleteByIds(Long[] ids) {
        return typeTemplateService.deleteByIds(ids);
    }
}
