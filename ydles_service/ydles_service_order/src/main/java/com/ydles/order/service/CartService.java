package com.ydles.order.service;

import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/19 11:01
 */
public interface CartService {
    /**
     * 添加购物车
     * @param skuId 要添加的商品skuId
     * @param number 数量
     * @param username 具体用户
     */
    void addCart(String skuId, Integer number, String username);

    /**
     * 显示当前用户购物车
     * @param username 用户名
     * @return 返回对应的购物车信息
     */
    Map<String, Object> showCart(String username);
}
