package com.ydles.seckill.task;

import com.ydles.seckill.dao.SeckillGoodsMapper;
import com.ydles.seckill.pojo.SeckillGoods;
import com.ydles.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Scz
 * @date 2022/4/24 16:53
 */
@Component
public class SecKillGoodsPushTask {
    public static final String SECKILL_GOODS_KEY = "seckill_goods_";
    public static final String SECKILL_GOODS_STOCK_COUNT_KEY = "seckill_goods_stock_count_";
    @Autowired
    SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    RedisTemplate redisTemplate;


    @Scheduled(cron = "0/30 * * * * *")
    public void loadSecKillGoodsToRedis() {

//        1. 查询所有符合条件的秒杀商品

//        1)获取时间段集合并循环遍历出每一个时间段
        List<Date> dateMenus = DateUtil.getDateMenus();
//        2)获取每一个时间段名称, 用于后续redis中key的设置
        for (Date dateMenu : dateMenus) {
            String redisExt = DateUtil.date2Str(dateMenu);
            String redisKey = SECKILL_GOODS_KEY + redisExt;
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
        //        3)状态必须为审核通过 status = 1
            criteria.andEqualTo("status", "1");
        //        4)商品库存个数 > 0
            criteria.andGreaterThan("stockCount", 0);
        //        5)秒杀商品开始时间 >= 当前时间段
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            criteria.andGreaterThan("startTime", simpleDateFormat.format(dateMenu));
        //        6)秒杀商品结束<当前时间段 +2 小时
            criteria.andLessThan("endTime", simpleDateFormat.format(DateUtil.addDateHour(dateMenu, 2)));
        //        7)排除之前已经加载到Redis缓存中的商品数据
            Set keys = redisTemplate.boundHashOps(redisKey).keys();
            if(keys != null && keys.size() > 0){
                criteria.andNotIn("id", keys);
            }
        //        8)执行查询获取对应的结果集
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);
            //        2. 将秒杀商品存入缓存
            for (SeckillGoods seckillGood : seckillGoods) {
                // 商品信息
                redisTemplate.boundHashOps(redisKey).put(seckillGood.getId(), seckillGood);
                // 库存信息
                redisTemplate.boundValueOps(SECKILL_GOODS_STOCK_COUNT_KEY + seckillGood.getId()).
                        set(seckillGood.getStockCount());
            }
        }

    }
}
