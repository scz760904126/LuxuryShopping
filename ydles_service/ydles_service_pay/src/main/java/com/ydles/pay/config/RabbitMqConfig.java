package com.ydles.pay.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Scz
 * @date 2022/4/22 10:50
 */
@Configuration
public class RabbitMqConfig {
    public static final String ORDER_PAY="order_pay";

    @Bean
    public Queue queue(){
        return  new Queue(ORDER_PAY);
    }
}
