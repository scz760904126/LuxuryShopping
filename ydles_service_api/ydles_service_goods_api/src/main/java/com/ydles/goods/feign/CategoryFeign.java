package com.ydles.goods.feign;

import com.ydles.entity.Result;
import com.ydles.goods.pojo.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Scz
 * @date 2022/4/15 19:11
 */
@FeignClient(name = "goods")
public interface CategoryFeign {
    @GetMapping("/category/{id}")
    Result<Category> findById(@PathVariable Integer id);
}
