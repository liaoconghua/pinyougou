package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.ItemCat;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.service.ItemCatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {
    @Reference
    private ItemCatService itemCatService;

    /**
     * 分页查询数据
     */
    @GetMapping("/findByPage")
    public PageResult findByPage(Integer pageNum, Integer pageSize, Long parentId, ItemCat searchEntity) {
        return itemCatService.findByPage(pageNum, pageSize, parentId, searchEntity);
    }

    /**
     * 根据父级id查询商品分类
     */
    @GetMapping("/findItemCatByParentId")
    public List<ItemCat> findItemCatByParentId(Long parentId) {
        return itemCatService.findItemCatByParentId(parentId);
    }

    /**
     * 保存商品分类
     */
    @PostMapping("/save")
    public boolean save(@RequestBody ItemCat itemCat) {
        try {
            return itemCatService.save(itemCat);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 修改商品分类
     */
    @PostMapping("/update")
    public boolean update(@RequestBody ItemCat itemCat) {
        return itemCatService.update(itemCat);
    }

    /**
     * 删除商品分类
     */
    @GetMapping("/deleteByIds")
    public boolean deleteByIds(Long[] ids) {
        return itemCatService.deleteByIds(ids);
    }

}
