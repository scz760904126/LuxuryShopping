package com.ydles.page.service;

/**
 * @author Scz
 * @date 2022/4/15 19:13
 */
public interface PageService {
    /**
     * 根据spuId 生成对应的静态话页面
     * @param spuId 商品的spuId
     */
    void generateHtml(String spuId);
}
