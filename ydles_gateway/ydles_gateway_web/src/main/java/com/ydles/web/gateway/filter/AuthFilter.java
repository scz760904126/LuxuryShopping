package com.ydles.web.gateway.filter;

import com.ydles.web.gateway.utils.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Scz
 * @date 2022/4/17 21:59
 */
@Component
public class AuthFilter implements Ordered, GlobalFilter {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public static final String LOGIN_URL = "http://localhost:8001/api/oauth/toLogin";


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        // 1.登录请求，放行
        if (!UrlUtil.hasAuthorize(path)) {
            //放行
            return chain.filter(exchange);
        }
        // 2. 用户访问资源
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        // 通过cookie中的uid判断当前用户是否登录过
        HttpCookie cookie = cookies.getFirst("uid");
        // 2.1当前用户还未登录
        if(cookie == null){
//            // 拒绝访问
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return response.setComplete();
            // 重定向到登录页面
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().set("Location", LOGIN_URL + "?from=" + path);
            return response.setComplete();
        }
        // 2.2当前用户已登录还需要进一步校验jti是否存在
        String jti = cookie.getValue();
        String jwt = stringRedisTemplate.opsForValue().get(jti);
        // redis中 jti不存在
        if(StringUtils.isEmpty(jwt)){
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().set("Location", LOGIN_URL + "?from=" + path);
            return response.setComplete();
        }
        // 3.拼接 jwt到请求头中
        request.mutate().header("Authorization", "Bearer " + jwt);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
