package com.ydles.order.listener;

import com.alibaba.fastjson.JSON;
import com.ydles.order.config.RabbitMqConfig;
import com.ydles.order.pojo.Task;
import com.ydles.order.service.TaskService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Scz
 * @date 2022/4/21 11:01
 */
@Component
public class DelTaskListener {
    @Autowired
    TaskService taskService;

    @RabbitListener(queues = RabbitMqConfig.CG_BUYING_FINISHADDPOINT)
    public void receiveMsg(String msg){
        System.out.println("order服务接收到user完成添加积分的消息");
        taskService.delTask(JSON.parseObject(msg, Task.class));
    }
}
