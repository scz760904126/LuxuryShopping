package com.ydles.system.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * ip拦截
 * @author Scz
 * @date 2022/4/12 9:08
 */
@Component
public class IpFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        String hostString = remoteAddress.getHostString();
        // 在黑名单中
        if(hostString.equals("1.1.1.1")){
            // 拒绝访问
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        // 继续执行其他过滤器链
        return chain.filter(exchange);
    }

    /**
     * 指定过滤器的顺序，越小越先执行
     * @return 顺序值
     */
    @Override
    public int getOrder() {
        return 1;
    }
}
