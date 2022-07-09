package com.ydles.search.dao;

import com.ydles.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Scz
 * @date 2022/4/13 19:59
 */
@Repository
public interface ESManagerMapper extends ElasticsearchRepository<SkuInfo, Long> {
}
