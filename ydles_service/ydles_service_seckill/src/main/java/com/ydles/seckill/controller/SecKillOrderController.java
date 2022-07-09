package com.ydles.seckill.controller;

import com.ydles.entity.Result;
import com.ydles.entity.StatusCode;
import com.ydles.seckill.config.TokenDecode;
import com.ydles.seckill.service.SecKillOrderService;
import com.ydles.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Scz
 * @date 2022/4/25 11:21
 */
@RestController
@RequestMapping("/seckillorder")
public class SecKillOrderController {
    @Autowired
    TokenDecode tokenDecode;
    @Autowired
    SecKillOrderService secKillOrderService;


    @RequestMapping("/add")
    public Result<Object> add(@RequestParam("time") String time, @RequestParam("id") String id){
        String username = tokenDecode.getUserInfo().get("username");
        boolean flag = secKillOrderService.add(username, id, time);
        if(flag){
            return new Result<>(true, StatusCode.OK, "秒杀下单成功");
        }else{
            return new Result<>(false, StatusCode.ERROR, "秒杀下单失败");
        }
    }
}
