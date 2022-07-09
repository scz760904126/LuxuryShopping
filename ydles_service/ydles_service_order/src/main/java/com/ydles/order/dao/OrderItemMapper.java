package com.ydles.order.dao;

import com.ydles.order.pojo.OrderItem;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
@Repository
public interface OrderItemMapper extends Mapper<OrderItem> {

}
