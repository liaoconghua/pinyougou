package com.pinyougou.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.ItemCat;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.ItemCatMapper;
import com.pinyougou.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Transactional
@Service(interfaceName = "com.pinyougou.service.ItemCatService")
public class ItemCatServiceImpl implements ItemCatService {
    @Autowired
    private ItemCatMapper itemCatMapper;

    /**
     * 分页查询数据
     */
    @Override
    public PageResult findByPage(Integer pageNum, Integer pageSize, Long parentId, ItemCat searchEntity) {
        try {
            // 创建ItemCat封装查询条件
            ItemCat itemCat = new ItemCat();
            itemCat.setParentId(parentId);
            PageInfo<ItemCat> pageInfo = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(
                    () -> itemCatMapper.select(itemCat));
            return new PageResult(pageInfo.getList(), pageInfo.getTotal());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageResult();
    }

    /**
     * 根据父级id查询商品分类
     */
    @Override
    public List<ItemCat> findItemCatByParentId(Long parentId) {
        try {
            // 创建ItemCat封装查询条件
            ItemCat itemCat = new ItemCat();
            itemCat.setParentId(parentId);

            return itemCatMapper.select(itemCat);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 保存商品分类
     */
    @Override
    public boolean save(ItemCat itemCat) {
        int row = itemCatMapper.insert(itemCat);
        return row > 0;
    }

    /**
     * 修改商品分类
     */
    @Override
    public boolean update(ItemCat itemCat) {
        int row = itemCatMapper.updateByPrimaryKeySelective(itemCat);
        return row > 0;
    }

    /**
     * 删除商品分类
     */
    @Override
    public boolean deleteByIds(Long[] ids) {
        Example example = new Example(ItemCat.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));

        List<ItemCat> itemCats = new ArrayList<>();
        for (Long id : ids) {
            Example selExample = new Example(ItemCat.class);
            Example.Criteria selExampleCriteria = selExample.createCriteria();
            selExampleCriteria.andEqualTo("parentId", id);
            itemCats = itemCatMapper.selectByExample(selExample);
            if (itemCats.size() > 0) {
                ArrayList<Long> itemCatsLongs = new ArrayList<>();
                for (ItemCat itemCat : itemCats) {
                    Long itemCatId = itemCat.getId();
                    itemCatsLongs.add(itemCatId);
                }
                if (itemCatsLongs.size() > 0) {
                    deleteByParentId(itemCatsLongs);
                }
            }
        }
        if (itemCats.size() > 0) {
            deleteByParentId(Arrays.asList(ids));
        }

        int row = itemCatMapper.deleteByExample(example);
        return row > 0;
    }

    /**
     * 通过父级id删除元素
     */
    private void deleteByParentId(List<Long> longs) {
        Example delExample = new Example(ItemCat.class);
        Example.Criteria delExampleCriteria = delExample.createCriteria();
        delExampleCriteria.andIn("parentId", longs);
        itemCatMapper.deleteByExample(delExample);
    }
}
