package com.ydles.seckill.web.aspect;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.RateLimiter;
import com.ydles.entity.Result;
import com.ydles.entity.StatusCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Scz
 * @date 2022/4/25 21:39
 */
@Aspect //标明切面类
@Component
@Scope  // 作用域
public class AccessLimitAop {
    @Autowired
    HttpServletResponse response;


    //设置令牌的生成速率  每秒生成两个令牌存入桶中
    private RateLimiter rateLimiter = RateLimiter.create(2.0);

    // 定义切点
    @Pointcut("@annotation(com.ydles.seckill.web.aspect.AccessLimit)")
    public void limitFlow(){

    }

    // 环绕增强
    @Around("limitFlow()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint){
        // 限流逻辑
        // 拿令牌
        boolean result = rateLimiter.tryAcquire();
        Object proceed = null;
        if(result){
            // 拿到令牌
            try {
                proceed = proceedingJoinPoint.proceed();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }else{
            // 拿不到令牌
            Result<Object> objectResult = new Result<>(false, StatusCode.ACCESSLIMIT, "限流了，请稍微再试");
            String s = JSON.toJSONString(objectResult);
            // 将信息写回到用户的浏览器上
            writeMsg(response,s);
        }
        return proceed;
    }

    // 给用户写回数据
    public void writeMsg(HttpServletResponse response, String msg){
        ServletOutputStream outputStream = null;
        try {
            // 设置头，指定返回的数据是json格式
            response.setContentType("application/json;charset=utf-8");
            outputStream = response.getOutputStream();
            // 写数据
            outputStream.write(msg.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            // 关闭流
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
