package com.ydles.user.dao;

import com.ydles.user.pojo.PointLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Scz
 * @date 2022/4/21 10:29
 */
@Repository
public interface PointLogMapper extends Mapper<PointLog> {
    @Select("select * from tb_point_log where order_id = #{orderId}")
    PointLog findPointLogByOrderId(@Param("orderId") String orderId);
}
