package com.ydles.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author Scz
 * @date 2022/4/19 16:46
 * feign拦截器
 * 只要feign远程调用，就把上一层请求头jwt待到这次请求中
 */
@Component
public class FeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 拿到上一层请求头中的jwt
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes != null){
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()){
                String headerName = headerNames.nextElement();
                if("Authorization".equalsIgnoreCase(headerName)){
                    // Bearer jwt
                    String jwt = request.getHeader(headerName);
                    requestTemplate.header(headerName, jwt);
                    break;
                }
            }
        }
    }
}
