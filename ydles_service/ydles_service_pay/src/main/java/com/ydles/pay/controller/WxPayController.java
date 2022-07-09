package com.ydles.pay.controller;

import com.alibaba.fastjson.JSON;
import com.github.wxpay.sdk.WXPayUtil;
import com.ydles.entity.Result;
import com.ydles.entity.StatusCode;
import com.ydles.pay.config.RabbitMqConfig;
import com.ydles.pay.service.WxPayService;
import com.ydles.utils.XmlConvert;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/21 20:34
 */
@RestController
@RequestMapping("/wxpay")
public class WxPayController {
    @Autowired
    WxPayService wxPayService;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 支付接口
     *
     * @param orderId 订单id
     * @param money   金额
     * @return 结果
     */
    @GetMapping("/nativePay")
    public Result<Map<String, String>> nativePay(@RequestParam("orderId") String orderId, @RequestParam("money") Integer money) {
        Map<String, String> resultMap = wxPayService.nativePay(orderId, money);
        return new Result<>(true, StatusCode.OK, "微信下单成功", resultMap);
    }

    @RequestMapping("/notify")
    public void wxPayNotify(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 微信支付端回推数据处理
            String xml = XmlConvert.convertToString(request.getInputStream());
            Map<String, String> map = WXPayUtil.xmlToMap(xml);
            // 收到微信的反馈数据
            if ("SUCCESS".equalsIgnoreCase(map.get("return_code"))) {
                // 查询订单是否真的完成
                Map<String, String> queryMap = wxPayService.orderQuery(map.get("out_trade_no"));
                if ("SUCCESS".equalsIgnoreCase(queryMap.get("return_code")) && "SUCCESS".equalsIgnoreCase(queryMap.get("result_code"))
                        && "SUCCESS".equalsIgnoreCase(queryMap.get("trade_state"))) {
                    // 真的完成了
                    String transactionId = queryMap.get("transaction_id");
                    String orderId = queryMap.get("out_trade_no");

                    Map<String, String> msg = new HashMap<>(16);
                    msg.put("transactionId", transactionId);
                    msg.put("orderId", orderId);
                    // 向order pay的mq中发送消息，使order服务修改订单状态
                    rabbitTemplate.convertAndSend(RabbitMqConfig.ORDER_PAY, JSON.toJSONString(msg));

                    // 还需要向全双工的交换机发送消息，使客户端页面接受
                    rabbitTemplate.convertAndSend("paynotify", "", orderId);

                    // 给微信成功的响应数据
                    response.setContentType("text/xml");
                    String data = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
                    response.getWriter().write(data);
                }else {
                    //输出错误原因
                    System.out.println(queryMap.get("err_code_des"));
                }
            }else{
                System.out.println(map.get("err_code_des"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PutMapping("/close/")
    public Result<Map<String, String>> closeOrder(@RequestParam("orderId") String orderId){
        Map<String, String> resultMap = wxPayService.closeOrder(orderId);
        return new Result<>(true, StatusCode.OK, "关闭订单成功", resultMap);
    }

    @GetMapping("/query/")
    public Result<Map<String, String>> queryOrder(@RequestParam("orderId") String orderId){
        Map<String, String> resultMap = wxPayService.orderQuery(orderId);
        return new Result<>(true, StatusCode.OK, "查询订单成功", resultMap);
    }
}
