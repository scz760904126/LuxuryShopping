package com.ydles.business.listener;

import okhttp3.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Scz
 * @date 2022/4/13 16:40
 */
@Component
public class AdListener {

    /**
     * 监听rabbitmq中队列的改变，发生改变后向nginx发送请求执行lua脚本，重新加载轮播图
     * @param position 改变的position值
     */
    @RabbitListener(queues = "ad_update_queue")
    public void receiveMsg(String position){
        // 发送请求，构造okHttpClient对象
        OkHttpClient client = new OkHttpClient();
        // 构造url
        String url = "http://192.168.200.128/ad_update?position=" + position;
        // 构造请求
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        // 请求加入调度，异步get请求回调
        call.enqueue(new Callback() {
            // 失败回调
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("发送请求失败");
            }

            // 成功回调
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("发送请求成功" + response.message());
            }
        });
    }
}
