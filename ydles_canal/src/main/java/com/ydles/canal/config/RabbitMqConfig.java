package com.ydles.canal.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Scz
 * @date 2022/4/13 16:24
 */
@Configuration
public class RabbitMqConfig {
    public static final String AD_UPDATE_QUEUE = "ad_update_queue";

    @Bean
    public Queue queue() {
        return new Queue(AD_UPDATE_QUEUE, true);
    }

    /**
     * 定义上架队列名称
     */
    public static final String SEARCH_ADD_QUEUE = "search_add_queue";

    /**
     * 定义上架队列
     * @return bean
     */
    @Bean(SEARCH_ADD_QUEUE)
    public Queue SEARCH_ADD_QUEUE() {
        return new Queue(SEARCH_ADD_QUEUE);
    }

    /**
     * 上架交换机名称
     */
    public static final String GOODS_UP_EXCHANGE = "goods_up_exchange";

    /**
     * 定义上架交换机
     * @return bean
     */
    @Bean(GOODS_UP_EXCHANGE)
    public Exchange GOODS_UP_EXCHANGE() {
        return ExchangeBuilder.fanoutExchange(GOODS_UP_EXCHANGE).durable(true).build();
    }

    /**
     * 上架队列与上架交换机绑定关系构建
     */
    @Bean
    public Binding binding(@Qualifier(SEARCH_ADD_QUEUE) Queue queue,
                           @Qualifier(GOODS_UP_EXCHANGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

    /**
     * 定义下架队列名称
     */
    public static final String SEARCH_DEL_QUEUE = "search_del_queue";

    /**
     * 定义下架队列
     * @return bean
     */
    @Bean(SEARCH_DEL_QUEUE)
    public Queue SEARCH_DEL_QUEUE() {
        return new Queue(SEARCH_DEL_QUEUE);
    }

    /**
     * 下架交换机名称
     */
    public static final String GOODS_DOWN_EXCHANGE = "goods_down_exchange";

    /**
     * 定义下架交换机
     * @return bean
     */
    @Bean(GOODS_DOWN_EXCHANGE)
    public Exchange GOODS_DOWN_EXCHANGE() {
        return ExchangeBuilder.fanoutExchange(GOODS_DOWN_EXCHANGE).durable(true).build();
    }

    /**
     * 下架队列与下架交换机绑定关系构建
     */
    @Bean
    public Binding bindingDel(@Qualifier(SEARCH_DEL_QUEUE) Queue queue,
                           @Qualifier(GOODS_DOWN_EXCHANGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

}
