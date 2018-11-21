package com.pinyougou.mapper;

import com.pinyougou.common.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BrandMapper extends Mapper<Brand> {
    /**
     * 查询全部品牌
     */
    List<Brand> findAll();

    /**
     * 通过id查询品牌
     */
    Brand findBrandById(Long id);

    /**
     * 查询所有的品牌(id与name)
     */
    @Select("select id, name as text from tb_brand order by id asc")
    List<Map<String, Object>> findAllByIdAndName();
}
