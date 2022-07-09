package com.ydles.order.controller;

import com.netflix.discovery.converters.Auto;
import com.ydles.entity.Result;
import com.ydles.entity.StatusCode;
import com.ydles.order.config.TokenDecode;
import com.ydles.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/19 14:23
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;
    @Autowired
    TokenDecode tokenDecode;

    @GetMapping("/addCart")
    public Result<Object> addCart(@RequestParam("skuId") String skuId, @RequestParam("num") Integer num) {
        String username = tokenDecode.getUserInfo().get("username");
        cartService.addCart(skuId, num, username);
        return new Result<>(true, StatusCode.OK, "添加购物车成功");
    }

    @GetMapping("/list")
    public Map<String, Object> showCart() {
        String username = tokenDecode.getUserInfo().get("username");
        return cartService.showCart(username);
    }
}
