package com.ydles.search.controller;

import com.ydles.entity.Page;
import com.ydles.search.pojo.SkuInfo;
import com.ydles.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/14 10:25
 */
@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    SearchService searchService;

    @GetMapping
    @ResponseBody   // 返回的是json数据
    public Map<String, Object> search(@RequestParam Map<String, String> searchMap) {
        handleSearchMap(searchMap);
        return searchService.search(searchMap);
    }

    /**
     * 处理searchMap中的特殊字符
     * key以 "spec_"开头的， value把 + --> %2b
     *
     * @param searchMap 输入参数
     */
    private void handleSearchMap(Map<String, String> searchMap) {
        for (Map.Entry<String, String> entry : searchMap.entrySet()) {
            if (entry.getKey().startsWith("spec_")) {
                searchMap.put(entry.getKey(), entry.getValue().replace("+", "%2B"));
            }
        }
    }

    /**
     * 承接搜索页面的请求 /search/list/keywords=包包&brand=PRADA&spec_
     *
     * @param searchMap 搜索参数map
     * @param model     视图
     * @return 跳转的页面
     */
    @GetMapping("/list")
    public String list(@RequestParam Map<String, String> searchMap, Model model) {
        handleSearchMap(searchMap);
        Map<String, Object> resultMap = searchService.search(searchMap);
        model.addAttribute("result", resultMap);
        model.addAttribute("searchMap", searchMap);

        //拼接url
        StringBuilder url = new StringBuilder("/search/list");
        // 如果有搜索条件
        if (searchMap != null && searchMap.size() > 0) {
            url.append("?");
            //拼接
            for (String paramKey : searchMap.keySet()) {
                //排除特殊情况
                if (!"sortRule".equals(paramKey) && !"sortField".equals(paramKey) && !"pageNum".equals(paramKey)) {
                    url.append(paramKey).append("=").append(searchMap.get(paramKey)).append("&");
                }
            }
            String urlString = url.toString();
            //除去最后的&
            urlString = urlString.substring(0, urlString.length() - 1);
            model.addAttribute("url", urlString);
        } else {
            model.addAttribute("url", url);
        }

        // 分页
        Page<SkuInfo> page = new Page<>(Long.parseLong(String.valueOf(resultMap.get("total"))),
                Integer.parseInt(String.valueOf(resultMap.get("pageNum"))),
                Integer.parseInt(String.valueOf(resultMap.get("pageSize"))));
        model.addAttribute("page", page);
        return "search";
    }
}
