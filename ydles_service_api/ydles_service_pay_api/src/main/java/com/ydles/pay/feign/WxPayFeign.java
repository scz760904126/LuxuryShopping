package com.ydles.pay.feign;

import com.ydles.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/21 20:49
 */
@FeignClient(name = "pay")
public interface WxPayFeign {
    @GetMapping("/wxpay/nativePay")
    Result<Map<String, String>> nativePay(@RequestParam("orderId") String orderId, @RequestParam("money") Integer money);

    @PutMapping("/wxpay/close/")
    Result<Map<String, String>> closeOrder(@RequestParam("orderId") String orderId);

    @GetMapping("/wxpay/query/")
    Result<Map<String, String>> queryOrder(@RequestParam("orderId") String orderId);
}
