package com.ydles.goods.controller;
import com.ydles.entity.PageResult;
import com.ydles.entity.Result;
import com.ydles.entity.StatusCode;
import com.ydles.goods.service.SkuService;
import com.ydles.goods.pojo.Sku;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/sku")
public class SkuController {


    @Autowired
    private SkuService skuService;

    /**
     * 查询全部数据
     * @return
     */
    @GetMapping
    public Result<List<Sku>> findAll(){
        List<Sku> skuList = skuService.findAll();
        return new Result<>(true, StatusCode.OK,"查询成功",skuList) ;
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Sku> findById(@PathVariable String id){
        Sku sku = skuService.findById(id);
        return new Result<>(true,StatusCode.OK,"查询成功",sku);
    }


    /***
     * 新增数据
     * @param sku
     * @return
     */
    @PostMapping
    public Result<Object> add(@RequestBody Sku sku){
        skuService.add(sku);
        return new Result<>(true,StatusCode.OK,"添加成功");
    }


    /***
     * 修改数据
     * @param sku
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Sku sku,@PathVariable String id){
        sku.setId(id);
        skuService.update(sku);
        return new Result(true,StatusCode.OK,"修改成功");
    }


    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable String id){
        skuService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件搜索品牌数据
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search" )
    public Result findList(@RequestParam Map searchMap){
        List<Sku> list = skuService.findList(searchMap);
        return new Result(true,StatusCode.OK,"查询成功",list);
    }


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result findPage(@RequestParam Map searchMap, @PathVariable  int page, @PathVariable  int size){
        Page<Sku> pageList = skuService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 根据spuId 查询skuList
     * @param spuId id
     * @return skuList
     */
    @GetMapping("/spu/{spuId}")
    public List<Sku> findSkuListBySpuId(@PathVariable("spuId") String spuId){
        Map<String, Object> searchMap = new HashMap<>();
        // 后门 spuId = all，查询所有
        if(!"all".equals(spuId)){
            searchMap.put("spuId", spuId);
        }
        // 只查询status 为1的
        searchMap.put("status", "1");
        return skuService.findList(searchMap);
    }

    @PutMapping("/decr/count")
    public Result<Object> decrCount(@RequestParam("username") String username){
        skuService.decrCount(username);
        return new Result<>(true,StatusCode.OK,"减库存成功");
    }

    @PutMapping("/resumeStock")
    public Result<Object> resumeStock(@RequestParam("skuId") String skuId, @RequestParam("num") Integer num){
        skuService.resumeStock(skuId, num);
        return new Result<>(true,StatusCode.OK,"回滚库存成功");
    }

}
