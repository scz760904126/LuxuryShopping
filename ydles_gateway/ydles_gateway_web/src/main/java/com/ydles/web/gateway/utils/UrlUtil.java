package com.ydles.web.gateway.utils;

/**
 * @author Scz
 * @date 2022/4/18 13:28
 */
public class UrlUtil {
    public static String filterPath = "/api/wseckillorder, /api/seckill/**,/api/wxpay,/api/worder/**," +
            "/api/user/**,/api/address/**,/api/wcart/**,/api/cart/**,/api/categoryReport/**,/api/wxpay/**" +
            "/api/orderConfig/**,/api/order/**,/api/orderItem/**,/api/orderLog/**,/api/preferential/**," +
            "/api/returnCause/**,/api/returnOrder/**,/api/returnOrderItem/**";

    /**
     * 判断传来的url是否需要令牌
     * @param url url
     * @return 是否需要令牌
     */
    public static boolean hasAuthorize(String url){
        String[] split = filterPath.replace("**", "").split(",");
        for(String s : split){
            if(url.startsWith(s)){
                return true;
            }
        }
        return false;
    }
}
