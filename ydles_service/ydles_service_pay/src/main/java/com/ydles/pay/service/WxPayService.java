package com.ydles.pay.service;

import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/21 20:15
 */
public interface WxPayService {
    /**
     * 微信native支付，进行下单
     * @param orderId 订单id
     * @param money 订单金额
     * @return 调用微信sdk方法得到的map
     */
    Map<String, String> nativePay(String orderId, Integer money);

    /**
     * 查询订单完成状态
     * @param orderId 订单id
     * @return 查询结果
     */
    Map<String, String> orderQuery(String orderId);

    /**
     * 关闭订单
     * @param orderId 订单id
     * @return 关闭结果
     */
    Map<String, String> closeOrder(String orderId);

}
