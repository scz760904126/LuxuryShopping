package com.github.wxpay.sdk;

import java.io.InputStream;

/**
 * @author Scz
 * @date 2022/4/21 20:11
 */
public class Myconfig extends WXPayConfig{
    @Override
    String getAppID() {
        return "wxababcd122d1618eb";
    }

    @Override
    String getMchID() {
        return "1611671554";
    }

    @Override
    String getKey() {
        return "ydlclass66666688888YDLCLASS66688";
    }

    @Override
    InputStream getCertStream() {
        return null;
    }

    @Override
    IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain() {
            @Override
            public void report(String s, long l, Exception e) {

            }

            @Override
            public DomainInfo getDomain(WXPayConfig wxPayConfig) {
                return new DomainInfo("api.mch.weixin.qq.com", true);
            }
        };
    }
}
