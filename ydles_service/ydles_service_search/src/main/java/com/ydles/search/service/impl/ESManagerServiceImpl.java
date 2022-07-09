package com.ydles.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.ydles.goods.feign.SkuFeign;
import com.ydles.goods.pojo.Sku;
import com.ydles.search.dao.ESManagerMapper;
import com.ydles.search.pojo.SkuInfo;
import com.ydles.search.service.ESManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/13 20:04
 */
@Service
public class ESManagerServiceImpl implements ESManagerService {
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    SkuFeign skuFeign;
    @Autowired
    ESManagerMapper esManagerMapper;

    /**
     * 创建索引库和映射
     */
    @Override
    public void createIndexAndMapping() {
        // 创建索引
        elasticsearchTemplate.createIndex(SkuInfo.class);
        // 创建映射
        elasticsearchTemplate.putMapping(SkuInfo.class);
    }

    /**
     * 将所有sku 导入es
     */
    @Override
    public void importData() {
        List<Sku> skuList = skuFeign.findSkuListBySpuId("all");
        // 导入es
        List<SkuInfo> skuInfoList = new ArrayList<>();
        for (Sku sku : skuList) {
            // 转换成中间变量
            String skuStr = JSON.toJSONString(sku);
            SkuInfo skuInfo = JSON.parseObject(skuStr, SkuInfo.class);
            // 需求： Sku.spe 原本是string  要转换为 SkuInfo.specMap map类型
            skuInfo.setSpecMap(JSON.parseObject(sku.getSpec(), Map.class));
            skuInfoList.add(skuInfo);
        }
        esManagerMapper.saveAll(skuInfoList);
    }

    /**
     * 根据spuId 将对应的skuList导入es
     *
     * @param spuId id
     */
    @Override
    public void importDataBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        // 导入es
        List<SkuInfo> skuInfoList = new ArrayList<>();
        for (Sku sku : skuList) {
            // 转换成中间变量
            String skuStr = JSON.toJSONString(sku);
            SkuInfo skuInfo = JSON.parseObject(skuStr, SkuInfo.class);
            // 需求： Sku.spe 原本是string  要转换为 SkuInfo.specMap map类型
            skuInfo.setSpecMap(JSON.parseObject(sku.getSpec(), Map.class));
            skuInfoList.add(skuInfo);
        }
        esManagerMapper.saveAll(skuInfoList);
    }

    @Override
    public void deleteDataBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        for (Sku sku : skuList) {
            // 根据sku id 从es中删除
            esManagerMapper.deleteById(Long.parseLong(sku.getId()));
        }
    }
}
