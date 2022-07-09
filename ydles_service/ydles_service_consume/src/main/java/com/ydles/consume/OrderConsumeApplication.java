package com.ydles.consume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author Scz
 * @date 2022/4/25 17:01
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = "com.ydles.consume.dao")
public class OrderConsumeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderConsumeApplication.class,args);
    }
}