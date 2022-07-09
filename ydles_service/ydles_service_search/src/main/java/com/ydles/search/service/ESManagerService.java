package com.ydles.search.service;

/**
 * @author Scz
 * @date 2022/4/13 20:01
 */
public interface ESManagerService {
    /**
     * 创建索引库和映射
     */
    void createIndexAndMapping();


    /**
     * 将所有sku 导入es
     */
    void importData();

    /**
     * 根据spuId 将对应的skuList导入es
     * @param spuId id
     */
    void importDataBySpuId(String spuId);

    /**
     * 根据spuId 将对应的skuList从es中删除
     * @param spuId id
     */
    void deleteDataBySpuId(String spuId);
}
