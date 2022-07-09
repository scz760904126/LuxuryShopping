package com.ydles.pay;

import com.github.wxpay.sdk.Myconfig;
import com.github.wxpay.sdk.WXPay;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

/**
 * @author Scz
 * @date 2022/4/21 20:12
 */
@SpringBootApplication
@EnableEurekaClient
public class PayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }
    @Bean
    public WXPay wxPay(){
        Myconfig myconfig = new Myconfig();
        try {
            return new WXPay(myconfig);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
