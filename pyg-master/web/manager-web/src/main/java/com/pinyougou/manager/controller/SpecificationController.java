package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.util.StringUtil;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.common.pojo.Specification;
import com.pinyougou.common.pojo.SpecificationOption;
import com.pinyougou.service.SpecificationService;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {
    @Reference(timeout = 10000)
    private SpecificationService specificationService;

    /**
     * 查询一页数据
     */
    @GetMapping("findByPage")
    public PageResult findByPage(Integer pageNum, Integer pageSize, Specification searchEntity) {
        if (StringUtil.isNotEmpty(searchEntity.getSpecName())) {
            searchEntity.setSpecName(new String(searchEntity.getSpecName()
                    .getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
        }
        return specificationService.findByPage(pageNum, pageSize, searchEntity);
    }

    /**
     * 保存规格
     */
    @PostMapping("/save")
    public boolean save(@RequestBody Specification specification) {
        return specificationService.save(specification);
    }

    /**
     * 更新界面数据回显
     */
    @GetMapping("/findSpecification")
    public List<SpecificationOption> findSpecification(Long id) {
        return specificationService.findSpecification(id);
    }

    /**
     * 更新规格
     */
    @PostMapping("/update")
    public boolean update(@RequestBody Specification specification) {
        return specificationService.update(specification);
    }

    /**
     * 删除规格
     */
    @GetMapping("/deleteByIds")
    public boolean deleteByIds(Long[] ids) {
        return specificationService.deleteByIds(ids);
    }

    /**
     * 查询所有的规格(id与specName)
     */
    @GetMapping("/findSpecList")
    public List<Map<String, Object>> findSpecList() {
        return specificationService.findAllByIdAndName();
    }

}
