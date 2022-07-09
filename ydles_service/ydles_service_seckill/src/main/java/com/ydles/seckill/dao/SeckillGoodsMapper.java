package com.ydles.seckill.dao;

import com.ydles.seckill.pojo.SeckillGoods;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface SeckillGoodsMapper extends Mapper<SeckillGoods> {

  
}
