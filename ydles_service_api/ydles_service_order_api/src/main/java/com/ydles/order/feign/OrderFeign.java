package com.ydles.order.feign;

import com.ydles.entity.Result;
import com.ydles.order.pojo.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Scz
 * @date 2022/4/20 11:17
 */
@FeignClient(name = "order")
public interface OrderFeign {
    @PostMapping("/order")
    Result<Object> add(@RequestBody Order order);

    @GetMapping("/order/{id}")
    Result<Order> findById(@PathVariable String id);

}
