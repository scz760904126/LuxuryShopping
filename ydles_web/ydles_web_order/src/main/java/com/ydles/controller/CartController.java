package com.ydles.controller;

import com.ydles.entity.Result;
import com.ydles.entity.StatusCode;
import com.ydles.order.feign.CartFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/19 15:50
 * 处理购物车相关的请求
 */
@Controller
@RequestMapping("/wcart")
public class CartController {
    @Autowired
    CartFeign cartFeign;

    @GetMapping("/list")
    String list(Model model){
        Map<String, Object> map = cartFeign.showCart();
        model.addAttribute("items", map);
        return "cart";
    }

    @GetMapping("/add")
    @ResponseBody
    public Result<Map<String, Object>> add(String skuId, Integer num){
        cartFeign.addCart(skuId, num);
        // 重新查询
        Map<String, Object> map = cartFeign.showCart();
        return new Result<>(true, StatusCode.OK, "修改购物车成功", map);
    }


}
