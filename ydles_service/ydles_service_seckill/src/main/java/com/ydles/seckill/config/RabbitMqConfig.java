package com.ydles.seckill.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Scz
 * @date 2022/4/25 15:51
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
