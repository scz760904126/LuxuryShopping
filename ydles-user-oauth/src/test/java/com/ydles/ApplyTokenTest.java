package com.ydles;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/16 21:41
 */
@SpringBootTest(classes = OAuthApplication.class)
@RunWith(SpringRunner.class)
public class ApplyTokenTest {
    @Autowired
    RestTemplate restTemplate;
    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecret;
    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Test
    public void applyToken(){
        // 负载均衡，选择名为user-auth的服务
        ServiceInstance serviceInstance = loadBalancerClient.choose("user-auth");
        URI uri = serviceInstance.getUri();
        // 1.构造url
        String url = uri.toString() + "oauth/token";
        // 2.1构建请求头
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", getHttpHeaders(clientId, clientSecret));
        // 2.2构建请求体
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        // 使用密码模式
        body.add("grant_type", "password");
        // 用户真正的账号和密码
        body.add("username", "itlils");
        body.add("password", "itlils");
        // 2.3构建请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // 对于错误的响应码 400 401等，不报错
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            // 相应当中有错误的话，处理方式
            @Override
            public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401){
                    super.handleError(url, method, response);
                }
            }
        });

        // 3.发送请求
        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        // 4.拿请求
        Map body1 = exchange.getBody();
        System.out.println(body1);
    }

    public String getHttpHeaders(String clientId, String clientSecret){
        // Basic basic64(客户端名称:客户端密码)
        StringBuilder sb = new StringBuilder();
        sb.append(clientId).append(":").append(clientSecret);
        return "Basic " + new String(Base64Utils.encode(new String(sb).getBytes()));
    }
}
