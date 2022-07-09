package com.ydles.order.listener;

import com.alibaba.fastjson.JSON;
import com.ydles.order.config.RabbitMqConfig;
import com.ydles.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/22 10:56
 */
@Component
public class OrderPayListener {
    @Autowired
    OrderService orderService;

    @RabbitListener(queues = RabbitMqConfig.ORDER_PAY)
    public void receiveMsg(String msg){
        Map<String,String> map = JSON.parseObject(msg, Map.class);
        // 修改order表中的数据，将支付状态由0改为1
        orderService.updatePayStatus(map.get("orderId"), map.get("transactionId"));
    }
}
