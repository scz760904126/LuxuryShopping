package com.ydles.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.ydles.search.pojo.SkuInfo;
import com.ydles.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Scz
 * @date 2022/4/14 9:39
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        // 多个搜索条件 使用 bool
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(searchMap != null && searchMap.size() > 0){
            // 1.1 按照用户输入查询  keywords=xxx
            if(StringUtils.isNotEmpty(searchMap.get("keywords"))){
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", searchMap.get("keywords"));
                boolQueryBuilder.must(matchQueryBuilder);
            }
            // 1.2 按照品牌查询 keywords=xxx&brand=xx
            if(StringUtils.isNotEmpty(searchMap.get("brand"))){
                // brandName 是 keyword类型，必须使用term查询
                TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("brandName", searchMap.get("brand"));
                // term查询一般放到filter中
                boolQueryBuilder.filter(termQueryBuilder);
            }
            // 1.3 按规格查询  keywords=xxx&brand=xx&spec_颜色=黑色&spec_尺寸=M
            for (String key : searchMap.keySet()) {
                if(key.startsWith("spec_")){
                    // 把 %2b 转为 +
                    String value = searchMap.get(key).replace("%2b", "+");
                    TermQueryBuilder query = QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword", value);
                    boolQueryBuilder.filter(query);
                }
            }

            // 1.4 按照价格进行区间查询 price=0-5000 price=30000
            if(StringUtils.isNotEmpty(searchMap.get("price"))){
                String price = searchMap.get("price");
                String[] split = price.split("-");
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").gte(split[0]);
                // price=0-5000
                if(split.length != 1){
                    rangeQueryBuilder.lte(split[1]);
                }
                boolQueryBuilder.filter(rangeQueryBuilder);
            }
        }
        // 1.搜索
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);

        // 2.1根据品牌 聚合
        String brandAggName = "skuBrand";
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms(brandAggName).field("brandName");
        nativeSearchQueryBuilder.addAggregation(brandAgg);

        // 2.2按照规格 聚合  spec.keyword
        String specAggName = "skuSpec";
        TermsAggregationBuilder specAgg = AggregationBuilders.terms(specAggName).field("spec.keyword");
        nativeSearchQueryBuilder.addAggregation(specAgg);


        // 3分页  pageNum=1&pageSize=30
        String pageNum = searchMap.get("pageNum");
        String pageSize = searchMap.get("pageSize");
        if(StringUtils.isEmpty(pageNum)){
            pageNum = "1";
        }
        if(StringUtils.isEmpty(pageSize)){
            pageSize = "20";
        }
        // 第0页是首页
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(pageNum) - 1, Integer.parseInt(pageSize));
        nativeSearchQueryBuilder.withPageable(pageRequest);

        // 4.排序 sortField=price&sortRule=Desc
        String sortField = searchMap.get("sortField");
        String sortRule = searchMap.get("sortRule");
        if(StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortRule)){
            FieldSortBuilder sort = SortBuilders.fieldSort(searchMap.get("sortField"));
            if("Asc".equals(sortRule)){
                sort.order(SortOrder.ASC);
            }else{
                sort.order(SortOrder.DESC);
            }
            nativeSearchQueryBuilder.withSort(sort);
        }

        // 5 高亮 要搜索的字段
        HighlightBuilder.Field field = new HighlightBuilder.Field("name");
        // 设置前签 和 后签
        field.preTags("<span style='color:red'>");
        field.postTags("</span>");
        nativeSearchQueryBuilder.withHighlightFields(field);

        /*
         1. 承接搜索条件
         2. 实体类类型
         3. 查询结果和对象映射

         skuInfos 查询结果
         */
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<T> list = new ArrayList<>();
                // 所有查询结果
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits.getHits()) {
                    // hit -> skuInfo对象
                    String sourceAsString = hit.getSourceAsString();
                    SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);

                    // 获取高亮 设置到name属性中
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    HighlightField highlightField = highlightFields.get("name");
                    StringBuilder sb = new StringBuilder();
                    for (Text fragment : highlightField.getFragments()) {
                        sb.append(fragment);
                    }
                    skuInfo.setName(new String(sb));

                    list.add((T) skuInfo);
                }

                return new AggregatedPageImpl<>(list, pageable, hits.getTotalHits(),searchResponse.getAggregations());
            }
        });

        Map<String, Object> resultMap = new HashMap<>(16);
        // 当前页的查询个数
        resultMap.put("total", skuInfos.getTotalElements());
        // 总页数
        resultMap.put("totalPages", skuInfos.getTotalPages());
        // 数据的集合
        resultMap.put("rows", skuInfos.getContent());

        // 品牌聚合的结果
        StringTerms brandTerms = (StringTerms)skuInfos.getAggregation(brandAggName);
        List<String> brandList = brandTerms.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString).collect(Collectors.toList());
        resultMap.put("brandList",brandList);

        // 规格聚合的结果
        StringTerms specTerms = (StringTerms)skuInfos.getAggregation(specAggName);
        List<String> specList = specTerms.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString).collect(Collectors.toList());
        resultMap.put("specList",formatSpec(specList));

        // 将分页所需的内容也放入map中
        resultMap.put("pageNum", pageNum);
        resultMap.put("pageSize", pageSize);

        return resultMap;
    }

    /**
     * 将规格结果进行转换
     * @param specList 规格结果
     * @return 转换后的规格表， key: 颜色,value: Set(黑色，白色，蓝色)
     */
    public Map<String, Set<String>> formatSpec(List<String> specList){
        Map<String, Set<String>> resultMap = new HashMap<>(8);
        for (String s : specList) {
            // s ==> "{‘颜色’:'黑色', '版本':'6GB+128GB'}"
            Map<String, String> map = JSON.parseObject(s, Map.class);
            for (String key : map.keySet()) {
                // valueSet存储当前key所对应的所有值 黑色，白色，蓝色
                Set<String> valueSet = resultMap.getOrDefault(key, new HashSet<>());
                // key颜色   value黑色
                valueSet.add(map.get(key));
                resultMap.put(key, valueSet);
            }
        }
        return resultMap;
    }
}
