package com.ydles.goods.dao;

import com.ydles.goods.pojo.Spec;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface SpecMapper extends Mapper<Spec> {
    /**
     * 根据分类名称查询规格
     */
    @Select("SELECT s.`name`, s.`options` FROM tb_category c LEFT JOIN tb_spec s on c.template_id = s.template_id WHERE c.`name` = #{categoryName} ORDER BY s.seq")
    List<Map<String, Object>> findByCategoryName(@Param("categoryName") String categoryName);
}
