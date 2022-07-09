package com.ydles.seckill.dao;

import com.ydles.seckill.pojo.SeckillOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface SeckillOrderMapper extends Mapper<SeckillOrder> {

    @Select("select * from tb_seckill_order where seckill_id = #{id} and user_id = #{username}")
    SeckillOrder getOrderInfoByUserNameAndGoodsId(@Param("username") String username, @Param("id") String id);
}
