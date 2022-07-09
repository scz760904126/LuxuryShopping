package com.ydles.user.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * @author Scz
 * @date 2022/4/21 9:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_point_log")
public class PointLog {
    private String orderId;
    private String userId;
    private Integer point;
}
