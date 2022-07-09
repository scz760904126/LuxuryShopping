package com.ydles.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import com.ydles.canal.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Scz
 * @date 2022/4/13 17:18
 */
@CanalEventListener
public class SpuListener {
    public static final String MARKETABLE = "is_marketable";

    @Autowired
    RabbitTemplate rabbitTemplate;

    @ListenPoint(schema = "ydles_goods", table = "tb_spu")
    public void goodsUp(CanalEntry.EventType eventType, CanalEntry.RowData rowData){
        // isMarketable
        Map<String, String> oldData = new HashMap<>();
        rowData.getBeforeColumnsList().forEach(column -> oldData.put(column.getName(), column.getValue()));
        Map<String, String> newData = new HashMap<>();
        rowData.getAfterColumnsList().forEach(column -> newData.put(column.getName(), column.getValue()));
        // 监听到商品上架  0 -> 1
        if("0".equals(oldData.get(MARKETABLE)) && "1".equals(newData.get(MARKETABLE))){
            // mq 发送spuId
            // 发送到对应的交换机，由于采用发布订阅模式，监听者都可以收到消息
            String spuId = newData.get("id");
            System.out.println(spuId + "商品上架了!");
            rabbitTemplate.convertAndSend(RabbitMqConfig.GOODS_UP_EXCHANGE,"", spuId);
        }
        // 监听到商品下架  1 -> 0
        if("1".equals(oldData.get(MARKETABLE)) && "0".equals(newData.get(MARKETABLE))){
            // mq 发送spuId
            // 发送到对应的交换机，由于采用发布订阅模式，监听者都可以收到消息
            String spuId = newData.get("id");
            System.out.println(spuId + "商品下架了!");
            rabbitTemplate.convertAndSend(RabbitMqConfig.GOODS_DOWN_EXCHANGE,"", spuId);
        }
    }

}
