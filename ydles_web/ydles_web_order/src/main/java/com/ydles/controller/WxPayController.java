package com.ydles.controller;

import com.ydles.order.feign.OrderFeign;
import com.ydles.order.pojo.Order;
import com.ydles.pay.feign.WxPayFeign;
import com.ydles.utils.XmlConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/21 20:53
 */
@Controller
@RequestMapping("/wxpay")
public class WxPayController {
    @Autowired
    OrderFeign orderFeign;
    @Autowired
    WxPayFeign wxPayFeign;


    @GetMapping
    public String wxPay(@RequestParam String orderId, Model model){
        Order order = orderFeign.findById(orderId).getData();
        // 订单为空 或者已经支付
        if(order == null || !"0".equals(order.getPayStatus())){
            return "fail";
        }
        Integer payMoney = order.getPayMoney();
        Map<String, String> mapResult = wxPayFeign.nativePay(orderId, payMoney).getData();
        if(mapResult == null){
            return "fail";
        }
        mapResult.put("orderId", orderId);
        mapResult.put("payMoney", payMoney + "");
        model.addAllAttributes(mapResult);
        return "wxpay";
    }

    @GetMapping("/toPaySuccess")
    public String toPaySuccess(String payMoney, Model model){
        model.addAttribute("payMoney", payMoney);
        return "paysuccess";
    }
}
