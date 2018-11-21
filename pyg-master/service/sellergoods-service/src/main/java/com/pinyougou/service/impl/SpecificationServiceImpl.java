package com.pinyougou.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.common.pojo.Specification;
import com.pinyougou.common.pojo.SpecificationOption;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.SpecificationService")
@Transactional
public class SpecificationServiceImpl implements SpecificationService {
    @Autowired
    private SpecificationMapper specificationMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    /**
     * 查询一页数据
     */
    @Override
    public PageResult findByPage(Integer pageNum, Integer pageSize, Specification searchEntity) {
        Example example = new Example(Specification.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(searchEntity.getSpecName())) {
            criteria.andLike("specName", "%" + searchEntity.getSpecName() + "%");
        }
        PageInfo<Specification> pageInfo = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> specificationMapper.selectByExample(example));
        return new PageResult(pageInfo.getList(), pageInfo.getTotal());
    }

    /**
     * 保存规格
     */
    @Override
    public boolean save(Specification specification) {
        int row = specificationMapper.insertSelective(specification);
        if (!specification.getSpecificationOptions().isEmpty()
                && specification.getSpecificationOptions().size() > 0) {
            specificationOptionMapper.save(specification);
        }
        return row > 0;
    }

    /**
     * 根据规格id查询更新数据回显
     */
    @Override
    public List<SpecificationOption> findSpecification(Long id) {
        SpecificationOption specificationOption = new SpecificationOption();
        specificationOption.setSpecId(id);
        return specificationOptionMapper.select(specificationOption);
    }

    /**
     * 更新规格
     */
    @Override
    public boolean update(Specification specification) {
        int row = specificationMapper.updateByPrimaryKeySelective(specification);

        //更新规格选项
        if (!specification.getSpecificationOptions().isEmpty()
                && specification.getSpecificationOptions().size() > 0) {
            SpecificationOption specificationOption = new SpecificationOption();
            specificationOption.setSpecId(specification.getId());

            Example optionExample = new Example(SpecificationOption.class);
            Example.Criteria criteria = optionExample.createCriteria();
            criteria.andEqualTo("specId", specificationOption.getSpecId());

            specificationOptionMapper.deleteByExample(optionExample);
            specificationOptionMapper.save(specification);
        }
        return row > 0;
    }

    /**
     * 删除规格
     */
    @Override
    public boolean deleteByIds(Long[] ids) {
        Example example = new Example(Specification.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));

        int row = specificationMapper.deleteByExample(example);

        //删除规格选项
        Example exampleOption = new Example(SpecificationOption.class);
        Example.Criteria criteriaOption = exampleOption.createCriteria();
        criteriaOption.andIn("specId", Arrays.asList(ids));

        specificationOptionMapper.deleteByExample(exampleOption);

        return row > 0;
    }

    /**
     * 查询所有的规格(id与specName)
     */
    @Override
    public List<Map<String, Object>> findAllByIdAndName() {
        try {
            return specificationMapper.findAllByIdAndName();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
