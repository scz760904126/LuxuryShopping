package com.ydles.search.service;

import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/14 9:35
 */
public interface SearchService {

    /**
     * 根据searchMap中的key - value对查询
     * @param searchMap 用户输入的搜索条件
     * @return 返回查询的结果包含多种信息
     */
    Map<String, Object> search(Map<String, String> searchMap);

}
