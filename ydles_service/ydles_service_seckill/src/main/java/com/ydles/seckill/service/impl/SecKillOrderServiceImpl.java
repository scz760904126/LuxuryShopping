package com.ydles.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.ydles.seckill.config.ConfirmMsgSender;
import com.ydles.seckill.config.RabbitMqConfig;
import com.ydles.seckill.dao.SeckillOrderMapper;
import com.ydles.seckill.pojo.SeckillGoods;
import com.ydles.seckill.pojo.SeckillOrder;
import com.ydles.seckill.service.SecKillOrderService;
import com.ydles.utils.IdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Scz
 * @date 2022/4/25 14:19
 */
@Service
public class SecKillOrderServiceImpl implements SecKillOrderService {
    public static final String SECKILL_GOODS_KEY = "seckill_goods_";
    public static final String SECKILL_GOODS_STOCK_COUNT_KEY = "seckill_goods_stock_count_";
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    IdWorker idWorker;
    @Autowired
    ConfirmMsgSender confirmMsgSender;
    @Autowired
    SeckillOrderMapper seckillOrderMapper;


    @Override
    public boolean add(String username, String id, String time) {

        // 防止恶意刷单
        int i = preventRepeatCommit(username, id);
        if(i != 1){
            return false;
        }

        // 防止重复购买
        SeckillOrder existOrder = seckillOrderMapper.getOrderInfoByUserNameAndGoodsId(username,id);
        if(existOrder != null){
            return false;
        }

        // 1.redis获取商品数据和库存量
        String goodsKey = SECKILL_GOODS_KEY + time;
        SeckillGoods secKillGoods = (SeckillGoods) redisTemplate.boundHashOps(goodsKey).get(Long.parseLong(id));
        if (secKillGoods == null) {
            return false;
        }
        // redis template使用string序列化
        String stockKey = SECKILL_GOODS_STOCK_COUNT_KEY + id;
        String stockCount = (String) redisTemplate.boundValueOps(stockKey).get();
        if (StringUtils.isEmpty(stockCount)) {
            return false;
        }
        // 库存不足
        int stock = Integer.parseInt(stockCount);
        if (stock <= 0) {
            return false;
        }
        // 2.预扣减库存
        Long decrement = redisTemplate.opsForValue().decrement(stockKey);
        // 如果库存为0，需要从redis中删除商品信息
        if (decrement <= 0) {
            redisTemplate.boundHashOps(goodsKey).delete(Long.parseLong(id));
            redisTemplate.delete(stockKey);
        }
        // 3.生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(Long.parseLong(id));
        // 每个用户只能买一个
        seckillOrder.setMoney(secKillGoods.getCostPrice());
        seckillOrder.setUserId(username);
        seckillOrder.setSellerId(secKillGoods.getSellerId());
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setStatus("0");
        // 4.订单数据发送到mq中
        confirmMsgSender.send("", RabbitMqConfig.SECKILL_ORDER_QUEUE, JSON.toJSONString(seckillOrder));
        return true;
    }

    /**
     * 防止重复提交
     * @param username 用户名
     * @param seckillId 商品id
     * @return 1 or 0
     */
    public int preventRepeatCommit(String username, String seckillId) {
        // 对于同一个用户同一个商品
        String key = "seckull_user_" + username + "_id_" + seckillId;
        Long increment = redisTemplate.opsForValue().increment(key);
        if(increment == 1){
            // 设置过期时间，3min之内不能再点
            redisTemplate.expire(key, 3, TimeUnit.MINUTES);
            return 1;
        }else{
            return 0;
        }
    }
}
