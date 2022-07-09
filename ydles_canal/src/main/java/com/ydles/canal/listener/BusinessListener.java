package com.ydles.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import com.ydles.canal.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Scz
 * @date 2022/4/13 15:47
 */
@CanalEventListener
public class BusinessListener {
    public static final String POSITION = "position";

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 监控business的广告表，当发生改变时，提取position值，发送到rabbitMQ
     * @param eventType 事件类型，insert update delete等
     * @param rowData   改变的行数据
     */
    @ListenPoint(schema = "ydles_business", table = "tb_ad")
    public void adUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        System.out.println("EventType:" + eventType);
        // 改变前的数据
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        beforeColumnsList.forEach(column -> System.out.println(column.getName() + " " + column.getValue()));
        System.out.println("----------------------");
        // 改变后的数据
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            // 找到对应position的地方
            if(POSITION.equals(column.getName())){
                String value = column.getValue();
                // 将变化的position值发送到mq中
                rabbitTemplate.convertAndSend("", RabbitMqConfig.AD_UPDATE_QUEUE, value);
            }
        }


    }
}
