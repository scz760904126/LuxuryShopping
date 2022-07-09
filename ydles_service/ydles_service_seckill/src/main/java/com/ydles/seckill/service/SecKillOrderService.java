package com.ydles.seckill.service;

/**
 * @author Scz
 * @date 2022/4/25 11:23
 */
public interface SecKillOrderService {
    /**
     * 秒杀下单
     * @param username 下单用户名
     * @param id 秒杀商品的id
     * @param time 时间
     * @return 秒杀是否成功
     */
    boolean add(String username, String id, String time);
}
