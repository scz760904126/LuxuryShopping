package com.ydles.search.listener;

import com.ydles.search.config.RabbitMqConfig;
import com.ydles.search.service.ESManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Scz
 * @date 2022/4/13 20:36
 */
@Component
public class GoodsUpListener {
    @Autowired
    ESManagerService esManagerService;

    @RabbitListener(queues = RabbitMqConfig.SEARCH_ADD_QUEUE)
    public void receiveMsg(String spuId){
        System.out.println(spuId + "商品上架啦");
        esManagerService.importDataBySpuId(spuId);
    }
}
