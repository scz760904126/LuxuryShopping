package com.ydles.seckill.feign;

import com.ydles.entity.Result;
import com.ydles.seckill.pojo.SeckillGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Scz
 * @date 2022/4/24 21:10
 */
@FeignClient(name = "seckill")
public interface SecKillGoodsFeign {
    @GetMapping("/seckillgoods/list")
    Result<List<SeckillGoods>> getListByTime(@RequestParam("time") String time);
}
