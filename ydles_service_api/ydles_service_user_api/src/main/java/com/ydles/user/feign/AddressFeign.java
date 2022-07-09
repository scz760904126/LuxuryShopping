package com.ydles.user.feign;

import com.ydles.user.pojo.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author Scz
 * @date 2022/4/19 20:19
 */
@FeignClient("user")
public interface AddressFeign {
    @GetMapping("/address/list")
    List<Address> findByUsername();

}
