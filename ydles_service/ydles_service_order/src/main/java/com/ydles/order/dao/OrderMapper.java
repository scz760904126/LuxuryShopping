package com.ydles.order.dao;

import com.ydles.order.pojo.Order;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
@Repository
public interface OrderMapper extends Mapper<Order> {

}
