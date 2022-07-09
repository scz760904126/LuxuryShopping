package com.ydles.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.ydles.entity.Result;
import com.ydles.goods.feign.SkuFeign;
import com.ydles.order.config.RabbitMqConfig;
import com.ydles.order.dao.*;
import com.ydles.order.pojo.*;
import com.ydles.order.service.CartService;
import com.ydles.order.service.OrderService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ydles.pay.feign.WxPayFeign;
import com.ydles.utils.IdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    public static final String CART = "cart_";
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    CartService cartService;
    @Autowired
    IdWorker idWorker;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    SkuFeign skuFeign;
    @Autowired
    TaskMapper taskMapper;
    @Autowired
    OrderLogMapper orderLogMapper;
    @Autowired
    WxPayFeign wxPayFeign;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 查询全部列表
     *
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }


    /**
     * 下单
     *
     * @param order 订单
     */
    @Override
    @Transactional
    public String add(Order order) {
        // 1. 获取购物车信息
        Map<String, Object> cartMap = cartService.showCart(order.getUsername());
        // 2. 向mysql的order表中添加数据
        String orderId = idWorker.nextId() + "";
        order.setId(orderId);
        order.setTotalNum((Integer) cartMap.get("totalNum"));
        order.setTotalMoney((Integer) cartMap.get("totalMoney"));
        order.setPayMoney((Integer) cartMap.get("totalMoney"));
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setBuyerRate("0");
        order.setSourceType("1");
        order.setOrderStatus("0");
        order.setPayStatus("0");
        order.setConsignStatus("0");
        order.setIsDelete("0");
        orderMapper.insertSelective(order);
        // 3. 向mysql中的order_item表中添加数据
        List<OrderItem> orderItemList = (List<OrderItem>) cartMap.get("orderItemList");
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderId(orderId);
            orderItem.setIsReturn("0");
            orderItemMapper.insertSelective(orderItem);
        }
        // 4. 远程调用skuFeign减库存
        skuFeign.decrCount(order.getUsername());

        // 5. 删除redis中的临时购物车数据
        redisTemplate.delete(CART + order.getUsername());

        // 6. 积分逻辑
        Task task = new Task();
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        task.setMqExchange(RabbitMqConfig.EX_BUYING_ADDPOINTUSER);
        task.setMqRoutingkey(RabbitMqConfig.CG_BUYING_ADDPOINT_KEY);
        // task中的RequestBody 需要 order_id user_id point
        Map<String, String> requestBoyd = new HashMap<>();
        requestBoyd.put("order_id", orderId);
        requestBoyd.put("user_id", order.getUsername());
        requestBoyd.put("point", cartMap.get("totalMoney") + "");
        task.setRequestBody(JSON.toJSONString(requestBoyd));
        taskMapper.insertSelective(task);

        // 向延时队列中发送消息
        rabbitTemplate.convertAndSend("", "queue.ordercreate", orderId);

        return orderId;
    }


    /**
     * 修改
     *
     * @param order
     */
    @Override
    public void update(Order order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     *
     * @param searchMap
     * @return
     */
    @Override
    public List<Order> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Order> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        return (Page<Order>) orderMapper.selectAll();
    }

    /**
     * 条件+分页查询
     *
     * @param searchMap 查询条件
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @Override
    public Page<Order> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        return (Page<Order>) orderMapper.selectByExample(example);
    }

    @Override
    @Transactional
    public void updatePayStatus(String orderId, String transactionId) {
        // 检测状态
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order != null && "0".equals(order.getPayStatus())){
            // 修改支付状态 已支付
            order.setPayStatus("1");
            // 修改订单状态 已付款
            order.setOrderStatus("1");
            // 设置流水号
            order.setTransactionId(transactionId);
            order.setPayTime(new Date());
            order.setUpdateTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);

            // 向订单日志 orderLog表里增添数据
            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker.nextId() + "");
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderId(orderId);
            orderLog.setOrderStatus("1");
            orderLog.setPayStatus("1");
            orderLog.setConsignStatus("0"); // 发货状态
            orderLog.setRemarks("已支付，微信流水号是:" + transactionId);
            orderLogMapper.insertSelective(orderLog);
        }
    }

    @Override
    @Transactional
    public void closeOrder(String orderId) {
        // 对于每笔订单，有ttl，因此对于正常成功订单有二次判断，对于非正常订单需要做额外处理
        // 1.验证订单是否存在
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order == null){
            throw new RuntimeException("该笔订单不能存在");
        }
        // 正常成功订单
        if("1".equals(order.getOrderStatus())){
            throw new RuntimeException("该笔订单已经支付完毕");
        }
        // 2.非正常支付订单(超时) 需要向微信 查询该笔订单
        Map<String, String> queryMap = wxPayFeign.queryOrder(orderId).getData();
        if ("SUCCESS".equalsIgnoreCase(queryMap.get("return_code")) && "SUCCESS".equalsIgnoreCase(queryMap.get("result_code"))
                && "SUCCESS".equalsIgnoreCase(queryMap.get("trade_state"))){
            // 2.1 该笔订单支付成功 (可能由于网络问题或者是用户在最后时间支付)
            // 修改订单状态
            updatePayStatus(orderId, queryMap.get("transaction_id"));
        }
        // todo: 各种result_code和trade_state状态都要考虑
        else{
            // 2.2 未支付
            // (1微信关闭订单
            wxPayFeign.closeOrder(orderId);
            // (2订单状态关闭
            order.setCloseTime(new Date());
            // 订单状态 0下单 1支付 2发货 3收货 4退货 9关闭
            order.setOrderStatus("9");
            orderMapper.updateByPrimaryKeySelective(order);

            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker.nextId() + "");
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderId(orderId);
            orderLog.setOrderStatus("9");
            orderLog.setPayStatus("0");
            orderLog.setConsignStatus("0"); // 发货状态
            orderLog.setRemarks("订单超时未支付成功");
            orderLogMapper.insertSelective(orderLog);

            // (3tb_sku 库存回滚
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            List<OrderItem> orderItems = orderItemMapper.select(orderItem);
            for (OrderItem item : orderItems) {
                skuFeign.resumeStock(item.getSkuId(), item.getNum());
            }
        }
    }

    @Override
    @Transactional
    public void batchSend(List<Order> orderList) {
        // 校验 order的有效性
        for (Order order : orderList) {
            if(order.getId() == null){
                throw new RuntimeException("订单号为空");
            }
            if(order.getShippingName() == null || order.getShippingCode() == null){
                throw new RuntimeException("订单物流信息有误");
            }
        }
        // 校验 order的状态
        for (Order order : orderList) {
            Order queryOrder = orderMapper.selectByPrimaryKey(order.getId());
            if(!"1".equals(queryOrder.getOrderStatus()) || !"0".equals(queryOrder.getConsignStatus())){
                throw new RuntimeException("订单状态有误");
            }
        }
        // 发货
        for (Order order : orderList) {
            order.setOrderStatus("2");
            order.setConsignStatus("1");
            order.setUpdateTime(new Date());
            order.setConsignTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);

            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker.nextId() + "");
            orderLog.setOperater("店小二");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderId(order.getId());
            orderLog.setOrderStatus("2");
            orderLog.setPayStatus("1");
            orderLog.setConsignStatus("1"); // 发货状态
            orderLog.setRemarks("发货成功");
            orderLogMapper.insertSelective(orderLog);
        }
    }

    @Override
    @Transactional
    public void take(String orderId, String operator) {
        // 校验
        if(StringUtils.isEmpty(orderId)){
            throw new RuntimeException("订单号为空");
        }
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order == null){
            throw new RuntimeException("订单为空");
        }
        if(!"1".equals(order.getConsignStatus())){
            throw new RuntimeException("订单尚未发货");
        }

        order.setConsignStatus("2");    // 已送达
        order.setOrderStatus("3");  // 已签收
        order.setUpdateTime(new Date());
        order.setEndTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);

        OrderLog orderLog = new OrderLog();
        orderLog.setId(idWorker.nextId() + "");
        orderLog.setOperater(operator);
        orderLog.setOperateTime(new Date());
        orderLog.setOrderId(order.getId());
        orderLog.setOrderStatus("3");
        orderLog.setPayStatus("1");
        orderLog.setConsignStatus("2"); // 发货状态
        orderLog.setRemarks("收获成功");
        orderLogMapper.insertSelective(orderLog);

    }

    @Autowired
    OrderConfigMapper orderConfigMapper;

    @Override
    public void autoTack() {
        // 查询发货超过 配置表中时间 的订单
        OrderConfig orderConfig = orderConfigMapper.selectByPrimaryKey("1");
        Integer takeTimeout = orderConfig.getTakeTimeout();
        // 推算拿几号之前发货的订单
        LocalDate now = LocalDate.now();
        LocalDate data = now.minusDays(takeTimeout);
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        // 已发货的
        criteria.andEqualTo("orderStatus", "2");
        criteria.andLessThanOrEqualTo("consignTime", data);
        List<Order> orders = orderMapper.selectByExample(example);
        // 修改订单状态
        for (Order order : orders) {
            take(order.getId(), "system");
        }
    }

    /**
     * 构建查询对象
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 订单id
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // 支付类型，1、在线支付、0 货到付款
            if (searchMap.get("payType") != null && !"".equals(searchMap.get("payType"))) {
                criteria.andEqualTo("payType", searchMap.get("payType"));
            }
            // 物流名称
            if (searchMap.get("shippingName") != null && !"".equals(searchMap.get("shippingName"))) {
                criteria.andLike("shippingName", "%" + searchMap.get("shippingName") + "%");
            }
            // 物流单号
            if (searchMap.get("shippingCode") != null && !"".equals(searchMap.get("shippingCode"))) {
                criteria.andLike("shippingCode", "%" + searchMap.get("shippingCode") + "%");
            }
            // 用户名称
            if (searchMap.get("username") != null && !"".equals(searchMap.get("username"))) {
                criteria.andLike("username", "%" + searchMap.get("username") + "%");
            }
            // 买家留言
            if (searchMap.get("buyerMessage") != null && !"".equals(searchMap.get("buyerMessage"))) {
                criteria.andLike("buyerMessage", "%" + searchMap.get("buyerMessage") + "%");
            }
            // 是否评价
            if (searchMap.get("buyerRate") != null && !"".equals(searchMap.get("buyerRate"))) {
                criteria.andLike("buyerRate", "%" + searchMap.get("buyerRate") + "%");
            }
            // 收货人
            if (searchMap.get("receiverContact") != null && !"".equals(searchMap.get("receiverContact"))) {
                criteria.andLike("receiverContact", "%" + searchMap.get("receiverContact") + "%");
            }
            // 收货人手机
            if (searchMap.get("receiverMobile") != null && !"".equals(searchMap.get("receiverMobile"))) {
                criteria.andLike("receiverMobile", "%" + searchMap.get("receiverMobile") + "%");
            }
            // 收货人地址
            if (searchMap.get("receiverAddress") != null && !"".equals(searchMap.get("receiverAddress"))) {
                criteria.andLike("receiverAddress", "%" + searchMap.get("receiverAddress") + "%");
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if (searchMap.get("sourceType") != null && !"".equals(searchMap.get("sourceType"))) {
                criteria.andEqualTo("sourceType", searchMap.get("sourceType"));
            }
            // 交易流水号
            if (searchMap.get("transactionId") != null && !"".equals(searchMap.get("transactionId"))) {
                criteria.andLike("transactionId", "%" + searchMap.get("transactionId") + "%");
            }
            // 订单状态
            if (searchMap.get("orderStatus") != null && !"".equals(searchMap.get("orderStatus"))) {
                criteria.andEqualTo("orderStatus", searchMap.get("orderStatus"));
            }
            // 支付状态
            if (searchMap.get("payStatus") != null && !"".equals(searchMap.get("payStatus"))) {
                criteria.andEqualTo("payStatus", searchMap.get("payStatus"));
            }
            // 发货状态
            if (searchMap.get("consignStatus") != null && !"".equals(searchMap.get("consignStatus"))) {
                criteria.andEqualTo("consignStatus", searchMap.get("consignStatus"));
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andEqualTo("isDelete", searchMap.get("isDelete"));
            }

            // 数量合计
            if (searchMap.get("totalNum") != null) {
                criteria.andEqualTo("totalNum", searchMap.get("totalNum"));
            }
            // 金额合计
            if (searchMap.get("totalMoney") != null) {
                criteria.andEqualTo("totalMoney", searchMap.get("totalMoney"));
            }
            // 优惠金额
            if (searchMap.get("preMoney") != null) {
                criteria.andEqualTo("preMoney", searchMap.get("preMoney"));
            }
            // 邮费
            if (searchMap.get("postFee") != null) {
                criteria.andEqualTo("postFee", searchMap.get("postFee"));
            }
            // 实付金额
            if (searchMap.get("payMoney") != null) {
                criteria.andEqualTo("payMoney", searchMap.get("payMoney"));
            }

        }
        return example;
    }

}
