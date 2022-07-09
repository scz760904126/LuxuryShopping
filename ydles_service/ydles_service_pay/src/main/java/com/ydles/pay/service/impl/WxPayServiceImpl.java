package com.ydles.pay.service.impl;

import com.github.wxpay.sdk.WXPay;
import com.ydles.pay.service.WxPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/21 20:18
 */
@Service
public class WxPayServiceImpl implements WxPayService {
    @Autowired
    WXPay wxPay;
    @Value("${wxpay.notify_url}")
    String notifyUrl;

    @Override
    public Map<String, String> nativePay(String orderId, Integer money) {
        Map<String, String> resultMap = null;
        try {
            Map<String, String> reqData = new HashMap<>();
            reqData.put("body", "下单测试");
            reqData.put("out_trade_no", orderId);
            // 金钱计算 要使用BigDecimal
//            BigDecimal rmb = new BigDecimal(money).multiply(new BigDecimal(100));
//            reqData.put("total_fee", String.valueOf(rmb));
            BigDecimal yuan = new BigDecimal("0.01");
            BigDecimal beishu = new BigDecimal(100);
            BigDecimal fen = yuan.multiply(beishu);
            fen = fen.setScale(0, BigDecimal.ROUND_UP);
            reqData.put("total_fee", String.valueOf(fen));

            reqData.put("spbill_create_ip", "123.12.12.123");
            reqData.put("notify_url", notifyUrl);
            reqData.put("trade_type", "NATIVE");
            resultMap = wxPay.unifiedOrder(reqData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    @Override
    public Map<String, String> orderQuery(String orderId) {
        Map<String, String> resultMap = null;
        try {
            Map<String, String> reqData = new HashMap<>();
            reqData.put("out_trade_no", orderId);
            resultMap = wxPay.orderQuery(reqData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    @Override
    public Map<String, String> closeOrder(String orderId) {
        Map<String, String> resultMap = null;
        try {
            Map<String, String> reqData = new HashMap<>();
            reqData.put("out_trade_no", orderId);
            resultMap = wxPay.closeOrder(reqData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }
}
