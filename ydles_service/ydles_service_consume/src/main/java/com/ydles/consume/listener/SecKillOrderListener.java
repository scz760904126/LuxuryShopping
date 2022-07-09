package com.ydles.consume.listener;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.ydles.consume.config.RabbitMqConfig;
import com.ydles.consume.service.SecKillOrderService;
import com.ydles.seckill.pojo.SeckillOrder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Scz
 * @date 2022/4/25 19:53
 */
@Component
public class SecKillOrderListener {
    @Autowired
    SecKillOrderService secKillOrderService;

    @RabbitListener(queues = RabbitMqConfig.SECKILL_ORDER_QUEUE)
    public void receiveMsg(Message msg, Channel channel){

        try {
            // 每次获取100条，削峰填谷
            channel.basicQos(100);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SeckillOrder seckillOrder = JSON.parseObject(msg.getBody(), SeckillOrder.class);
        // 业务下单
        int result = secKillOrderService.createOrder(seckillOrder);
        if(result > 0){
            try {
                // 手动ack，告诉mq收到消息,mq可以删除消息
                channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            // 出现问题，让消息重回队列
            try {
                channel.basicNack(msg.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
