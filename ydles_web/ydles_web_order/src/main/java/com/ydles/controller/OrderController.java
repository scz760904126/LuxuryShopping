package com.ydles.controller;

import com.ydles.entity.Result;
import com.ydles.order.feign.CartFeign;
import com.ydles.order.feign.OrderFeign;
import com.ydles.order.pojo.Order;
import com.ydles.order.pojo.OrderItem;
import com.ydles.user.feign.AddressFeign;
import com.ydles.user.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Scz
 * @date 2022/4/19 20:36
 * 处理订单页面相关的请求
 */
@Controller
@RequestMapping("/worder")
public class OrderController {
    @Autowired
    AddressFeign addressFeign;
    @Autowired
    CartFeign cartFeign;
    @Autowired
    OrderFeign orderFeign;

    @GetMapping("/ready/order")
    public String readyOrder(Model model){
        // 获取数据
        // 1 收件人信息
        List<Address> addressList = addressFeign.findByUsername();
        model.addAttribute("address", addressList);
        // 找到默认收件地址
        for (Address address : addressList) {
            if("1".equals(address.getIsDefault())){
                model.addAttribute("deAddr",address);
                break;
            }
        }

        // 2 购物车信息
        Map<String, Object> orderMap = cartFeign.showCart();
        // 细化购物车信息，进行分类
        // 总购物项
        List<OrderItem> orderItemList = (List<OrderItem>) orderMap.get("orderItemList");
        model.addAttribute("carts", orderItemList);
        Integer totalNum = (Integer) orderMap.get("totalNum");
        model.addAttribute("totalNum",totalNum);
        Integer totalMoney =  (Integer) orderMap.get("totalMoney");
        model.addAttribute("totalMoney",totalMoney);
        return "order";
    }

    @PostMapping("/add")
    @ResponseBody
    public Result<Object> add(@RequestBody Order order){
        return orderFeign.add(order);
    }

    @GetMapping("/pay")
    public String pay(String orderId, Model model){
        Result<Order> byId = orderFeign.findById(orderId);
        model.addAttribute("orderId", orderId);
        model.addAttribute("payMoney", byId.getData().getPayMoney());
        return "pay";
    }
}
