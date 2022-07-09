package com.ydles.seckill.config;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Scz
 * @date 2022/4/25 16:01
 */
@Component
public class ConfirmMsgSender implements RabbitTemplate.ConfirmCallback {
    public static final String MESSAGE_CONFIRM_KEY="message_confirm_";
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    RedisTemplate redisTemplate;

    public ConfirmMsgSender(RabbitTemplate rabbitTemplate) {
        // 本类和容器中的tmp建立联系
        this.rabbitTemplate = rabbitTemplate;
        // 回调给本类
        rabbitTemplate.setConfirmCallback(this);
    }

    /**
     * 自定义发送方法，功能增强
     *
     * @param exchange   交换机
     * @param routingKey 路由key
     * @param msg        消息
     */
    public void send(String exchange, String routingKey, String msg) {
        // 保存发送的信息到redis中
        Map<String, String> map = new HashMap<>(8);
        map.put("exchange", exchange);
        map.put("routingKey", routingKey);
        map.put("msg", msg);

        // 设置消息的唯一标识符
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        redisTemplate.boundHashOps(MESSAGE_CONFIRM_KEY + correlationData.getId()).putAll(map);
        rabbitTemplate.convertAndSend(exchange, routingKey, msg, correlationData);
    }

    /**
     * 回调方法，异步通知该消息是否发送成功
     *
     * @param correlationData 消息的唯一标识符
     * @param ack               是否发送成功
     * @param cause               原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String key = MESSAGE_CONFIRM_KEY + correlationData.getId();
        if (ack) {
            // 发送成功，删除redis中的数据
            redisTemplate.delete(key);
        } else {
            // 发送失败，重新发送
            Map<String, String> map = redisTemplate.boundHashOps(key).entries();
            String exchange = map.get("exchange");
            String routingKey = map.get("routingKey");
            String msg = map.get("msg");
            // 只重发一次，防止无限循环
            rabbitTemplate.convertAndSend(exchange, routingKey, msg);
        }
    }

}
