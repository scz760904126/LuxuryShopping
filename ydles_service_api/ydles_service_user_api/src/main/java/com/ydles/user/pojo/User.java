package com.ydles.user.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
@Table(name = "tb_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    @Id
    private String username;//用户名
    // 角色列表
//    List<Role> roleList;
    // 权限列表
//    List<Auth> authList;


    private String password;
    private String phone;
    private String email;
    private java.util.Date created;
    private java.util.Date updated;
    /**
     * 会员来源：1:PC，2：H5，3：Android，4：IOS
     */
    private String sourceType;
    private String nickName;
    /**
     * 真实姓名
     */
    private String name;
    /**
     * 使用状态（1正常 0非正常）
     */
    private String status;
    /**
     * 头像地址
     */
    private String headPic;
    private String qq;
    /**
     * 手机是否验证 （0否  1是）
     */
    private String isMobileCheck;
    /**
     * 邮箱是否检测（0否  1是）
     */
    private String isEmailCheck;
    /**
     * 性别，1男，0女
     */
    private String sex;
    /**
     * 会员等级
     */
    private Integer userLevel;
    /**
     * 积分
     */
    private Integer points;
    /**
     * 经验值
     */
    private Integer experienceValue;
    private java.util.Date birthday;
    private java.util.Date lastLoginTime;
}
