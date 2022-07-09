package com.ydles.goods.dao;

import com.ydles.goods.pojo.Sku;
import com.ydles.order.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
@Repository
public interface SkuMapper extends Mapper<Sku> {
    @Update("update tb_sku set num = num - #{num}, sale_num = sale_num + #{num} where id = #{skuId} and num >= #{num}")
    int decrCount(OrderItem orderItem);

    @Update("update tb_sku set num = num + #{num}, sale_num = sale_num - #{num} where id = #{skuId}")
    void resumeStock(@Param("skuId") String skuId, @Param("num") Integer num);
}
