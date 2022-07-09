package com.ydles.seckill.web.controller;

import com.ydles.entity.Result;
import com.ydles.entity.StatusCode;
import com.ydles.seckill.feign.SecKillOrderFeign;
import com.ydles.seckill.web.aspect.AccessLimit;
import com.ydles.seckill.web.util.CookieUtil;
import com.ydles.utils.RandomUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @author Scz
 * @date 2022/4/24 21:59
 */
@RestController
@RequestMapping("/wseckillorder")
public class SecKillOrderController {
    @Autowired
    SecKillOrderFeign secKillOrderFeign;
    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/add")
    @AccessLimit
    public Result<Object> add(@RequestParam("time") String time, @RequestParam("id") String id,
                              @RequestParam("random")String random){
        // 校验随机数是否正确
        String jti = readCookie();
        String redisRandomData = (String)redisTemplate.boundValueOps("random_code_" + jti).get();
        // 随机数过期、不存在 或者 与redis中的不相同
        if(StringUtils.isEmpty(redisRandomData) || !redisRandomData.equals(random)){
            return new Result<>(false,StatusCode.ERROR, "下单失败");
        }
        return secKillOrderFeign.add(time, id);
    }

    @GetMapping("/getToken")
    public String getToken(){
        // 生成随机数
        String randomString = RandomUtil.getRandomString();
        // 随机数存入redis  key:jti  value:random
        String jti = readCookie();
        redisTemplate.boundValueOps("random_code_" + jti).set(randomString, 10, TimeUnit.SECONDS);
        return randomString;
    }

    public String readCookie(){
        // 获取request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return CookieUtil.readCookie(request, "uid").get("uid");
    }
}
