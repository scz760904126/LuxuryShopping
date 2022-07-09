package com.ydles.goods.feign;

import com.ydles.entity.Result;
import com.ydles.goods.pojo.Goods;
import com.ydles.goods.pojo.Spu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Scz
 * @date 2022/4/15 19:00
 */
@FeignClient(name = "goods")
public interface SpuFeign {
    @GetMapping("/spu/findSpuById/{id}")
    Result<Spu> findSpuById(@PathVariable String id);
}
