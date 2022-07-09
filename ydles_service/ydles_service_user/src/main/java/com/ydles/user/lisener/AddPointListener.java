package com.ydles.user.lisener;

import com.alibaba.fastjson.JSON;
import com.ydles.order.pojo.Task;
import com.ydles.user.config.RabbitMqConfig;
import com.ydles.user.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Scz
 * @date 2022/4/21 10:18
 */
@Component
public class AddPointListener {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    UserService userService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    
    @RabbitListener(queues = RabbitMqConfig.CG_BUYING_ADDPOINT)
    public void receiveMsg(String msg){
        // 监听rabbitMq中的消息
        Task task = JSON.parseObject(msg, Task.class);
        if(task == null || StringUtils.isEmpty(task.getRequestBody())){
            return;
        }
        // 检测redis中是否存在该任务
        String value = redisTemplate.boundValueOps(task.getId() + "").get();
        if(!StringUtils.isEmpty(value)){
            return;
        }
        int i = userService.updateUserPoint(task);
        if(i  == 0){
            // 更新失败直接返回
            return;
        }else{
            // 更新成功，向rabbitMq中通知消息
            rabbitTemplate.convertAndSend(RabbitMqConfig.EX_BUYING_ADDPOINTUSER, RabbitMqConfig.CG_BUYING_FINISHADDPOINT_KEY,msg);
            System.out.println("user服务返回消息成功");
        }

    }
}
