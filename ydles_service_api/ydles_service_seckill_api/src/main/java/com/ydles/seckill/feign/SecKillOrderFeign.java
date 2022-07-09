package com.ydles.seckill.feign;

import com.ydles.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Scz
 * @date 2022/4/25 16:32
 */
@FeignClient(name = "seckill")
public interface SecKillOrderFeign {
    @RequestMapping("/seckillorder/add")
    Result<Object> add(@RequestParam("time") String time, @RequestParam("id") String id);
}
