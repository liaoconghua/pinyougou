package com.pinyougou.mapper;

import com.pinyougou.common.pojo.TypeTemplate;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface TypeTemplateMapper extends Mapper<TypeTemplate> {

    @Select("select id,name as text from tb_type_template order by id asc")
    List<Map<String, Object>> selectAllTypeTemplate();
}
