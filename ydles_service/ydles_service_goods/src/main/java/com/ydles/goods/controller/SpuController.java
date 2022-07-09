package com.ydles.goods.controller;
import com.ydles.entity.PageResult;
import com.ydles.entity.Result;
import com.ydles.entity.StatusCode;
import com.ydles.goods.pojo.Goods;
import com.ydles.goods.service.SpuService;
import com.ydles.goods.pojo.Spu;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/spu")
public class SpuController {


    @Autowired
    private SpuService spuService;

    /**
     * 查询全部数据
     * @return
     */
    @GetMapping
    public Result findAll(){
        List<Spu> spuList = spuService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",spuList) ;
    }

    /***
     * 根据ID查询数据
     * @param id 要查询的spu id
     * @return 结果
     */
    @GetMapping("/{id}")
    public Result<Goods> findById(@PathVariable String id){
        Goods goods = spuService.findById(id);
        return new Result<>(true,StatusCode.OK,"查询成功",goods);
    }

    @GetMapping("/findSpuById/{id}")
    public Result<Spu> findSpuById(@PathVariable String id){
        Spu spu = spuService.findSpuById(id);
        return new Result<>(true,StatusCode.OK,"查询成功",spu);
    }


    /***
     * 新增数据
     * @param goods 一个spu对象，和它所包含的sku集合
     * @return
     */
    @PostMapping
    public Result<Object> add(@RequestBody Goods goods){
        spuService.add(goods);
        return new Result(true,StatusCode.OK,"添加成功");
    }


    /***
     * 修改数据
     * @param goods 要修改的商品
     * @return 结果
     */
    @PutMapping()
    public Result<Object> update(@RequestBody Goods goods){
        spuService.update(goods);
        return new Result<>(true,StatusCode.OK,"修改成功");
    }


    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result<Object> delete(@PathVariable String id){
        spuService.delete(id);
        return new Result<>(true,StatusCode.OK,"删除成功");
    }

    /***
     * 根据ID真实删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/realDelete/{id}" )
    public Result<Object> realDelete(@PathVariable String id){
        spuService.realDelete(id);
        return new Result<>(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件搜索品牌数据
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search" )
    public Result findList(@RequestParam Map searchMap){
        List<Spu> list = spuService.findList(searchMap);
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
        Page<Spu> pageList = spuService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 审核商品
     * @param id 商品的spuId
     * @return
     */
    @PutMapping("/audit/{id}")
    public Result<Object> audit(@PathVariable("id") String id){
        spuService.audit(id);
        return new Result<>(true, StatusCode.OK, "审核通过");
    }

    /**
     * 下架商品
     * @param id 商品的spuId
     * @return
     */
    @PutMapping("/pull/{id}")
    public Result<Object> pull(@PathVariable("id") String id){
        spuService.pull(id);
        return new Result<>(true, StatusCode.OK, "下架通过");
    }

    /**
     * 上架商品
     * @param id 商品的spuId
     * @return
     */
    @PutMapping("/put/{id}")
    public Result<Object> put(@PathVariable("id") String id){
        spuService.put(id);
        return new Result<>(true, StatusCode.OK, "上架通过");
    }

    /**
     * 还原商品
     * @param id 商品的spuId
     * @return
     */
    @PutMapping("/resotre/{id}")
    public Result<Object> resotre(@PathVariable("id") String id){
        spuService.resotre(id);
        return new Result<>(true, StatusCode.OK, "恢复通过");
    }



}
