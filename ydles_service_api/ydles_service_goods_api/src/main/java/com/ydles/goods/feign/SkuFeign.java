package com.ydles.goods.feign;

import com.ydles.entity.Result;
import com.ydles.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Scz
 * @date 2022/4/13 19:51
 */
@FeignClient(name = "goods")
public interface SkuFeign {
    @GetMapping("/sku/spu/{spuId}")
    List<Sku> findSkuListBySpuId(@PathVariable("spuId") String spuId);

    @GetMapping("/sku/{id}")
    Result<Sku> findById(@PathVariable String id);

    @PutMapping("/sku/decr/count")
    Result<Object> decrCount(@RequestParam("username") String username);

    @PutMapping("/sku/resumeStock")
    Result<Object> resumeStock(@RequestParam("skuId") String skuId, @RequestParam("num") Integer num);
}
