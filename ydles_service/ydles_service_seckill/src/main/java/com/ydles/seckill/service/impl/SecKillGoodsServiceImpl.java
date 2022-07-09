package com.ydles.seckill.service.impl;

import com.ydles.seckill.dao.SeckillGoodsMapper;
import com.ydles.seckill.pojo.SeckillGoods;
import com.ydles.seckill.service.SecKillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Scz
 * @date 2022/4/24 21:03
 */
@Service
public class SecKillGoodsServiceImpl implements SecKillGoodsService {
    public static final String SECKILL_GOODS_KEY = "seckill_goods_";
    public static final String SECKILL_GOODS_STOCK_COUNT_KEY = "seckill_goods_stock_count_";
    @Autowired
    RedisTemplate redisTemplate;


    @Override
    public List<SeckillGoods> getListByTime(String time) {
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps(SECKILL_GOODS_KEY + time).values();
        // 获取当前真实的库存量
        for (SeckillGoods seckillGoods : seckillGoodsList) {
            String stockKey = SECKILL_GOODS_STOCK_COUNT_KEY + seckillGoods.getId();
            String stockCount = (String) redisTemplate.opsForValue().get(stockKey);
            seckillGoods.setStockCount(Integer.parseInt(stockCount));
        }
        return seckillGoodsList;
    }
}
