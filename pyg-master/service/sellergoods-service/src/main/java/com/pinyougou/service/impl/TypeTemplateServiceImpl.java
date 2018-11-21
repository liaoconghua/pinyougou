package com.pinyougou.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.common.pojo.SpecificationOption;
import com.pinyougou.common.pojo.TypeTemplate;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.TypeTemplateService")
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {
    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    /**
     * 查询一页数据
     */
    @Override
    public PageResult findByPage(Integer pageNum, Integer pageSize, TypeTemplate searchEntity) {
        Example example = new Example(TypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("name", "%" + searchEntity.getName() + "%");

        PageInfo<TypeTemplate> pageInfo = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(
                () -> typeTemplateMapper.selectByExample(example));
        return new PageResult(pageInfo.getList(), pageInfo.getTotal());
    }

    /**
     * 添加类型模板
     */
    @Override
    public void save(TypeTemplate typeTemplate) {
        typeTemplateMapper.insertSelective(typeTemplate);
    }

    /**
     * 修改类型模板
     */
    @Override
    public boolean update(TypeTemplate typeTemplate) {
        int row = typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
        return row > 0;
    }

    /**
     * 删除类型模板
     */
    @Override
    public boolean deleteByIds(Long[] ids) {
        Example example = new Example(TypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));

        int row = typeTemplateMapper.deleteByExample(example);
        return row > 0;
    }

    /**
     * 查询所有类型模板
     */
    @Override
    public List<Map<String, Object>> findAllTypeTemplate() {
        return typeTemplateMapper.selectAllTypeTemplate();
    }

    /**
     * 根据主键id查询类型模版
     */
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据模版id查询所有的规格与规格选项
     */
    @Override
    public List<Map> findSpecByTemplateId(Long id) {
        TypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        List<Map> specList = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);

        // 迭代模版中所有的规格
        for (Map spec : specList) {
            Example example = new Example(SpecificationOption.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("specId", spec.get("id").toString());
            List<SpecificationOption> specificationOptionList = specificationOptionMapper.selectByExample(example);
            spec.put("options", specificationOptionList);
        }

        return specList;
    }
}
