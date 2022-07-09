package com.ydles.order.listener;

import com.ydles.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Scz
 * @date 2022/4/22 20:38
 */
@Component
public class OrderTimeoutListener {
    @Autowired
    OrderService orderService;

    @RabbitListener(queues = "queue.ordertimeout")
    public void receiveMsg(String orderId){
        orderService.closeOrder(orderId);
    }
}
