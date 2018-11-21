package com.pinyougou.mapper;

import com.pinyougou.common.pojo.Specification;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpecificationMapper extends Mapper<Specification> {

    /**
     * 查询所有的规格(id与specName)
     */
    @Select("select id, spec_name as text from tb_specification order by id asc")
    List<Map<String, Object>> findAllByIdAndName();

}
