package com.ydles.seckill.web.controller;

import com.ydles.entity.Result;
import com.ydles.seckill.feign.SecKillGoodsFeign;
import com.ydles.seckill.pojo.SeckillGoods;
import com.ydles.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Scz
 * @date 2022/4/24 19:36
 */
@Controller
@RequestMapping("/wseckillgoods")
public class SecKillGoodsController {
    @Autowired
    SecKillGoodsFeign secKillGoodsFeign;

    @GetMapping("/toIndex")
    public String toIndex(){
        return "seckill-index";
    }

    @GetMapping("/timeMenus")
    @ResponseBody
    public List<String> timeMenus(){
        List<Date> dateMenus = DateUtil.getDateMenus();
        List<String> result = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Date dateMenu : dateMenus) {
            result.add(simpleDateFormat.format(dateMenu));
        }
        return result;
    }

    @GetMapping("/list")
    @ResponseBody
    public Result<List<SeckillGoods>> getListByTime(@RequestParam("time") String time){
        // time yyyy-MM-dd HH:mm  -> yyyyMMddHH
        String formatTime = DateUtil.formatStr(time);
        System.out.println(time);
        System.out.println(formatTime);
        return secKillGoodsFeign.getListByTime(formatTime);
    }

}
