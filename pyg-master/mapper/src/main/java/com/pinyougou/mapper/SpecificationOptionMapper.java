package com.pinyougou.mapper;

import com.pinyougou.common.pojo.Specification;
import com.pinyougou.common.pojo.SpecificationOption;
import tk.mybatis.mapper.common.Mapper;

public interface SpecificationOptionMapper extends Mapper<SpecificationOption> {

    /**
     * 保存规格选项
     */
    void save(Specification specification);
}
