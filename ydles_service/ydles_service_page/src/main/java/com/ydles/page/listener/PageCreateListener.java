package com.ydles.page.listener;

import com.ydles.page.config.RabbitMqConfig;
import com.ydles.page.service.PageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Scz
 * @date 2022/4/15 19:54
 */
@Component
public class PageCreateListener {
    @Autowired
    PageService pageService;

    @RabbitListener(queues = RabbitMqConfig.PAGE_CREATE_QUEUE)
    public void createPage(String spuId){
        pageService.generateHtml(spuId);
    }
}
