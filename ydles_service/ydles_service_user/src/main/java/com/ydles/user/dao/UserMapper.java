package com.ydles.user.dao;

import com.ydles.user.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
@Repository
public interface UserMapper extends Mapper<User> {

    @Update("update tb_user set points = points + #{point} where username = #{username}")
    int updateUserPoint(@Param("point") Integer point, @Param("username") String username);
}
