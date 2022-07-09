package com.ydles.task.job;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Scz
 * @date 2022/4/22 21:24
 */
@Component
public class OrderTask {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Scheduled(cron = "0 0 1 * * *")
    public void autoTake(){
        rabbitTemplate.convertAndSend("", "order_tack", "-");
    }
}
