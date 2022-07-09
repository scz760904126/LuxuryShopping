package com.ydles.page.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ydles.goods.feign.CategoryFeign;
import com.ydles.goods.feign.SkuFeign;
import com.ydles.goods.feign.SpuFeign;
import com.ydles.goods.pojo.Category;
import com.ydles.goods.pojo.Sku;
import com.ydles.goods.pojo.Spu;
import com.ydles.page.service.PageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/15 19:14
 */
@Service
public class PageServiceImpl implements PageService {

    @Autowired
    TemplateEngine templateEngine;
    @Value("${pagePath}")
    String pagePath;

    @Autowired
    SpuFeign spuFeign;
    @Autowired
    SkuFeign skuFeign;
    @Autowired
    CategoryFeign categoryFeign;

    /**
     * 根据spuId 生成对应的静态话页面
     *
     * @param spuId 商品的spuId
     */
    @Override
    public void generateHtml(String spuId) {
        // 上下文
        Context context = new Context();
        context.setVariables(getData(spuId));

        // 处理文件
        File dir = new File(pagePath);
        if(!dir.exists()){
            // E:\Project\ydles_item\spuId.html
            dir.mkdirs();
        }
        File file = new File(pagePath + "/" + spuId + ".html");
        Writer writer = null;
        try {
            writer = new FileWriter(file);
            // 模板  数据  写入的文件
            templateEngine.process("item", context, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 根据spuId 封装数据
     *
     * @param spuId id
     * @return 封装后的数据 map形式
     */
    public Map<String, Object> getData(String spuId) {
        Map<String, Object> resultMap = new HashMap<>(16);
        // 1. spu
        Spu spu = spuFeign.findSpuById(spuId).getData();
        resultMap.put("spu", spu);
        // 1.2 image
        String images = spu.getImages();
        if (StringUtils.isNotEmpty(images)) {
            String[] imageList = images.split(",");
            resultMap.put("imageList", imageList);
        }
        // 1.3 spec
        String specItems = spu.getSpecItems();
        if (StringUtils.isNotEmpty(specItems)) {
            Map specMap = JSONObject.parseObject(specItems, Map.class);
            resultMap.put("specificationList", specMap);
        }


        // 2. skuList
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        resultMap.put("skuList", skuList);
        // 3. category
        Category category1 = categoryFeign.findById(spu.getCategory1Id()).getData();
        Category category2 = categoryFeign.findById(spu.getCategory2Id()).getData();
        Category category3 = categoryFeign.findById(spu.getCategory3Id()).getData();
        resultMap.put("category1", category1);
        resultMap.put("category2", category2);
        resultMap.put("category3", category3);

        return resultMap;
    }
}
