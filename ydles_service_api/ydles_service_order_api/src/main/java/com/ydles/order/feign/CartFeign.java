package com.ydles.order.feign;

import com.ydles.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/19 15:44
 */
@FeignClient(name = "order")
public interface CartFeign {
    @GetMapping("/cart/addCart")
    Result<Object> addCart(@RequestParam("skuId") String skuId, @RequestParam("num") Integer num);
    @GetMapping("/cart/list")
    Map<String, Object> showCart();
}
