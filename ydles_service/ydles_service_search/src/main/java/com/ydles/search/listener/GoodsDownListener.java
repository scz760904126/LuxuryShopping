package com.ydles.search.listener;

import com.ydles.search.config.RabbitMqConfig;
import com.ydles.search.service.ESManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Scz
 * @date 2022/4/13 20:57
 */
@Component
public class GoodsDownListener {
    @Autowired
    ESManagerService esManagerService;

    @RabbitListener(queues = RabbitMqConfig.SEARCH_DEL_QUEUE)
    public void receiveMsg(String spuId){
        System.out.println(spuId + "商品下架啦");
        esManagerService.deleteDataBySpuId(spuId);
    }
}
