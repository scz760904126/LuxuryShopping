package com.ydles.seckill.service;

import com.ydles.seckill.pojo.SeckillGoods;

import java.util.List;

/**
 * @author Scz
 * @date 2022/4/24 21:02
 */
public interface SecKillGoodsService {
    /**
     * 查询时间段内的秒杀商品
     * @param time 时间段 yyyyMMddHH
     * @return 商品列表
     */
    List<SeckillGoods> getListByTime(String time);
}
