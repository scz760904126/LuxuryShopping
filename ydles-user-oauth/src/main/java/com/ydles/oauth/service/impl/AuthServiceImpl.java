package com.ydles.oauth.service.impl;

import com.ydles.oauth.service.AuthService;
import com.ydles.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Scz
 * @date 2022/4/16 22:23
 */
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    LoadBalancerClient loadBalancerClient;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Value("${auth.ttl}")
    long ttl;


    /**
     * 登录用户，并且返回申请的jwt令牌
     *
     * @param username     用户账号
     * @param password     用户密码
     * @param clientId     客户端id
     * @param clientSecret 客户端密码
     * @return 申请的jwt令牌
     */
    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        // 一. 向oauth2 申请令牌
        // 负载均衡，选择名为user-auth的服务
        ServiceInstance serviceInstance = loadBalancerClient.choose("user-auth");
        URI uri = serviceInstance.getUri();
        // 1.构造url
        String url = uri.toString() + "/oauth/token";
        // 2.1构建请求头
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", getHttpHeaders(clientId, clientSecret));
        // 2.2构建请求体
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        // 使用密码模式
        body.add("grant_type", "password");
        // 用户真正的账号和密码
        body.add("username", username);
        body.add("password", password);
        // 2.3构建请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // 对于错误的响应码 400 401等，不报错
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            // 相应当中有错误的话，处理方式
            @Override
            public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(url, method, response);
                }
            }
        });
        System.out.println(url);
        System.out.println(requestEntity);
        // 3.发送请求
        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        // 4.拿响应
        Map<String, String> responseMap = exchange.getBody();
        // 5.验证申请令牌是否成功
        if (responseMap == null || responseMap.get("access_token") == null ||
                responseMap.get("refresh_token") == null || responseMap.get("jti") == null) {
            throw new RuntimeException("申请令牌失败");
        }
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(responseMap.get("access_token"));
        authToken.setRefreshToken(responseMap.get("refresh_token"));
        authToken.setJti(responseMap.get("jti"));
        // 二. 向redis中存放 jti:jwt
        stringRedisTemplate.boundValueOps(authToken.getJti()).set(authToken.getAccessToken(), ttl, TimeUnit.SECONDS);
        return authToken;
    }

    public String getHttpHeaders(String clientId, String clientSecret) {
        // Basic basic64(客户端名称:客户端密码)
        StringBuilder sb = new StringBuilder();
        sb.append(clientId).append(":").append(clientSecret);
        return "Basic " + new String(Base64Utils.encode(new String(sb).getBytes()));
    }
}
