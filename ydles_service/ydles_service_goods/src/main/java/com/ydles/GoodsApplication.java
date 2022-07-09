package com.ydles;

import com.ydles.utils.IdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"com.ydles.goods.dao"})
public class GoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class);
    }

    @Value("${workerId}")
    private Long workerId;
    @Value("${datacenterId}")
    private Long datacenterId;
    @Bean
    public IdWorker idWorker(){
        return new IdWorker(workerId, datacenterId);
    }
}
