package com.ydles.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.ydles.goods.feign.SkuFeign;
import com.ydles.goods.feign.SpuFeign;
import com.ydles.goods.pojo.Sku;
import com.ydles.goods.pojo.Spu;
import com.ydles.order.pojo.OrderItem;
import com.ydles.order.service.CartService;
import com.ydles.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/19 11:02
 */
@Service
public class CartServiceImpl implements CartService {
    public static final String CART = "cart_";

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    SpuFeign spuFeign;
    @Autowired
    SkuFeign skuFeign;
    @Autowired
    IdWorker idWorker;

    /**
     * 添加购物车
     *
     * @param skuId    要添加的商品skuId
     * @param number   数量
     * @param username 具体用户
     */
    @Override
    public void addCart(String skuId, Integer number, String username) {
        String orderItemStr = (String) redisTemplate.boundHashOps(CART + username).get(skuId);
        Sku sku = skuFeign.findById(skuId).getData();
        Spu spu = spuFeign.findSpuById(sku.getSpuId()).getData();
        // 判断当前购物车里面是否有该商品
        OrderItem orderItem;
        if(!StringUtils.isEmpty(orderItemStr)){
            orderItem = JSON.parseObject(orderItemStr, OrderItem.class);
            orderItem.setNum(orderItem.getNum() + number);
            if(orderItem.getNum() < 1){
                redisTemplate.boundHashOps(CART + username).delete(skuId);
                return;
            }
            orderItem.setMoney(orderItem.getPrice() * orderItem.getNum());
            // 暂不考虑优惠活动
            orderItem.setPayMoney(orderItem.getPrice() * orderItem.getNum());
            orderItem.setWeight(sku.getWeight() * orderItem.getNum());
        }else{
            // 新加入购物车的商品
            orderItem = sku2OrderItem(spu, sku, number);
        }
        redisTemplate.boundHashOps(CART + username).put(skuId, JSON.toJSONString(orderItem));
    }

    /**
     * 显示当前用户购物车
     * @param username 用户名
     * @return 返回对应的购物车信息
     */
    @Override
    public Map<String, Object> showCart(String username) {
        Map<String, Object> resultMap = new HashMap<>();
        List<Object> values = redisTemplate.boundHashOps(CART + username).values();
        List<OrderItem> orderItemList = new ArrayList<>();
        Integer totalNum = 0;
        Integer totalMoney = 0;
        for (Object value : values) {
            OrderItem orderItem = JSON.parseObject((String) value, OrderItem.class);
            orderItemList.add(orderItem);
            totalNum += orderItem.getNum();
            totalMoney += orderItem.getPayMoney();
        }
        // 存放所有的商品
        resultMap.put("orderItemList", orderItemList);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalMoney", totalMoney);

        return resultMap;
    }

    private OrderItem sku2OrderItem(Spu spu, Sku sku, Integer number){
        OrderItem orderItem = new OrderItem();
        orderItem.setId(idWorker.nextId() + "");
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        orderItem.setSpuId(spu.getId());
        orderItem.setSkuId(sku.getId());
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setNum(number);
        orderItem.setMoney(sku.getPrice() * number);
        orderItem.setPayMoney(sku.getPrice() * number);
        orderItem.setImage(sku.getImage());
        orderItem.setWeight(sku.getWeight() * number);
        return orderItem;
    }
}
