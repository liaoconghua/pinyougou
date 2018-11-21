package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.Brand;
import com.pinyougou.service.BrandService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 查询所有品牌
     */
    @GetMapping("/findAll")
    public List<Brand> findAll() {
        return brandService.findAll(1, 100);
    }

    /**
     * 保存品牌
     */
    @PostMapping("/save")
    public boolean save(@RequestBody Brand brand) {
        return brandService.save(brand);
    }

    /**
     * 修改品牌
     */
    @PostMapping("/update")
    public boolean update(@RequestBody Brand brand) {
        return brandService.update(brand);
    }
}
