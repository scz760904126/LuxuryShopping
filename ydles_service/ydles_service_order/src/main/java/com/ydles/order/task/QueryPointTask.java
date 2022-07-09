package com.ydles.order.task;

import com.alibaba.fastjson.JSON;
import com.ydles.order.dao.TaskMapper;
import com.ydles.order.pojo.Task;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author Scz
 * @date 2022/4/21 9:59
 */
@Component
public class QueryPointTask {
    @Autowired
    TaskMapper taskMapper;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Scheduled(cron = "0/60 * * * * ?")
    public void queryPoint(){
        // 定时扫表
        List<Task> taskList = taskMapper.findTaskLessThanCurrentTime(new Date());

        for (Task task : taskList) {
            rabbitTemplate.convertAndSend(task.getMqExchange(), task.getMqRoutingkey(), JSON.toJSONString(task));
            System.out.println("order服务向mq中发送消息");
        }
    }
}
