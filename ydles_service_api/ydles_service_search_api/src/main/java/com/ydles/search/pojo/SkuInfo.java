package com.ydles.search.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 实体类对应的是es索引库中的数据
 * @author Scz
 * @date 2022/4/13 18:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "skuinfo", type = "docs")
public class SkuInfo implements Serializable {

    /**
     * 商品id，同时也是商品编号
     */
    @Id
    @Field(store = true, type = FieldType.Keyword)
    private Long id;

    /**
     * SKU名称
     */
    @Field(store = true, type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String name;

    /**
     * 商品价格，单位为：元
     */
    @Field(store = true, type = FieldType.Double)
    private Long price;

    /**
     * 库存数量
     */
    @Field(store = true, type = FieldType.Integer)
    private Integer num;

    /**
     * 商品图片
     */
    @Field(index = false, store = true, type = FieldType.Text)
    private String image;

    /**
     * 商品状态，1-正常，2-下架，3-删除
     */
    @Field(store = true, type = FieldType.Keyword)
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否默认
     */
    @Field(store = true, type = FieldType.Keyword)
    private String isDefault;

    /**
     * SPUID
     */
    @Field(store = true, type = FieldType.Long)
    private Long spuId;

    /**
     * 类目ID
     */
    @Field(store = true, type = FieldType.Long)
    private Long categoryId;

    /**
     * 类目名称
     */
    @Field(store = true, type = FieldType.Keyword)
    private String categoryName;

    /**
     * 品牌名称
     */
    @Field(store = true, type = FieldType.Keyword)
    private String brandName;

    /**
     * 规格
     */
    private String spec;

    /**
     * 规格参数
     */
    private Map<String, Object> specMap;

}