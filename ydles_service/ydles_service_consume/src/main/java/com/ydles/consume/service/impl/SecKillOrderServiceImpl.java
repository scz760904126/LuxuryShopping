package com.ydles.consume.service.impl;

import com.ydles.consume.dao.SeckillGoodsMapper;
import com.ydles.consume.dao.SeckillOrderMapper;
import com.ydles.consume.service.SecKillOrderService;
import com.ydles.seckill.pojo.SeckillOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Scz
 * @date 2022/4/25 19:57
 */
@Service
public class SecKillOrderServiceImpl implements SecKillOrderService {
    @Autowired
    SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    SeckillOrderMapper seckillOrderMapper;

    @Transactional
    @Override
    public int createOrder(SeckillOrder seckillOrder) {
        // 修改库存
        int affectRow = seckillGoodsMapper.updateStockCount(seckillOrder.getSeckillId());
        if(affectRow <= 0){
            return affectRow;
        }
        // 添加订单
        int res = seckillOrderMapper.insertSelective(seckillOrder);
        return res;
    }
}
