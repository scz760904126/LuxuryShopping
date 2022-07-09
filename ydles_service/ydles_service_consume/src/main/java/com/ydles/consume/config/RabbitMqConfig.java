package com.ydles.consume.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author Scz
 * @date 2022/4/25 17:03
 */
@Configuration
public class RabbitMqConfig {
    //秒杀商品订单消息
    public static final String SECKILL_ORDER_QUEUE="seckill_order";

    @Bean
    public Queue queue(){
        return new Queue(SECKILL_ORDER_QUEUE, true);
    }
}