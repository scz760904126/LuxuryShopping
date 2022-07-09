package com.ydles.goods.dao;

import com.ydles.goods.pojo.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface BrandMapper extends Mapper<Brand> {

    /**
     * 根据分类名称查询品牌
     */
    @Select("SELECT b.`name`, b.image from tb_category c LEFT JOIN tb_category_brand cb ON c.id = cb.category_id LEFT JOIN tb_brand b on cb.brand_id = b.id where c.`name` = #{categoryName} order by b.seq ")
    List<Map<String, Object>> findByCategoryName(@Param("categoryName") String categoryName);
}
