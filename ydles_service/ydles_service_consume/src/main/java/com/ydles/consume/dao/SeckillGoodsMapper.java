package com.ydles.consume.dao;

import com.ydles.seckill.pojo.SeckillGoods;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface SeckillGoodsMapper extends Mapper<SeckillGoods> {

    @Update("update tb_seckill_goods set stock_count = stock_count - 1 where id = #{seckillId} and stock_count >= 1")
    int updateStockCount(@Param("seckillId") Long seckillId);
}
