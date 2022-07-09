package com.ydles.system.filters;

import com.ydles.system.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Scz
 * @date 2022/4/12 15:54
 */
@Component
public class AuthorizeFilter implements Ordered, GlobalFilter {
    public static final String AUTHORIZE_TOKEN = "token";
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        if(path.contains("admin/login")){
            // 1.登录请求，放行
            return chain.filter(exchange);
        }
        // 2.访问资源，校验jwt
        // 前端规定 已登录的用户请求头中含有 token:jwt 这样的key-value对
        String jwt = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        // jwt为空
        if(jwt == null || jwt.length() == 0){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }else{
            try{
                // 校验jwt
                JwtUtil.parseJWT(jwt);
            } catch (Exception e) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
