package com.ydles.order.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Scz
 * @date 2022/4/21 9:13
 */
@Table(name = "tb_task_his")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskHis {
    @Id
    private Long id;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "delete_time")
    private Date deleteTime;

    @Column(name = "task_type")
    private String taskType;

    @Column(name = "mq_exchange")
    private String mqExchange;

    @Column(name = "mq_routingkey")
    private String mqRoutingkey;

    @Column(name = "request_body")
    private String requestBody;

    @Column(name = "status")
    private String status;

    @Column(name = "errormsg")
    private String errormsg;
}
