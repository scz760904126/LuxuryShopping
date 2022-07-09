package com.ydles.goods.controller;

import com.ydles.entity.PageResult;
import com.ydles.entity.Result;
import com.ydles.entity.StatusCode;
import com.ydles.goods.service.BrandService;
import com.ydles.goods.pojo.Brand;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 查询全部数据
     *
     * @return 返回前端的响应
     */
    @GetMapping
    public Result<Object> findAll() {
        List<Brand> brandList = brandService.findAll();
        return new Result<>(true, StatusCode.OK, "查询成功", brandList);
    }

    /***
     * 根据ID查询数据
     * @param id 查询id
     * @return 返回前端的响应
     */
    @GetMapping("/{id}")
    public Result<Object> findById(@PathVariable Integer id) {
        Brand brand = brandService.findById(id);
        return new Result<>(true, StatusCode.OK, "查询成功", brand);
    }


    /***
     * 新增数据
     * @param brand 新增品牌 需要加上@RequestBody注解，转化为json格式
     * @return 返回前端的响应
     */
    @PostMapping
    public Result<Object> add(@RequestBody Brand brand) {
        brandService.add(brand);
        return new Result<>(true, StatusCode.OK, "添加成功");
    }


    /***
     * 修改数据
     * @param brand 品牌
     * @param id 更新的品牌id
     * @return 返回前端的响应
     */
    @PutMapping(value = "/{id}")
    public Result<Object> update(@RequestBody Brand brand, @PathVariable Integer id) {
        brand.setId(id);
        brandService.update(brand);
        return new Result<>(true, StatusCode.OK, "修改成功");
    }


    /***
     * 根据ID删除品牌数据
     * @param id 要删除的品牌id
     * @return 返回前端的响应
     */
    @DeleteMapping(value = "/{id}")
    public Result<Object> delete(@PathVariable Integer id) {
        brandService.delete(id);
        return new Result<>(true, StatusCode.OK, "删除成功");
    }

    /***
     * 多条件搜索品牌数据
     * @param searchMap 条件搜索的map
     * @return 返回前端的响应
     */
    @GetMapping(value = "/search")
    public Result<List<Brand>> findList(@RequestParam Map<String, Object> searchMap) {
        List<Brand> list = brandService.findList(searchMap);
        return new Result<>(true, StatusCode.OK, "查询成功", list);
    }


    /***
     * 分页搜索实现
     * @param searchMap 条件搜索的map
     * @param page 当前页码
     * @param size 页大小
     * @return 返回前端的响应
     */
    @GetMapping(value = "/search/{page}/{size}")
    public Result<PageResult> findPage(@RequestParam Map<String, Object> searchMap, @PathVariable int page, @PathVariable int size) {
        Page<Brand> pageList = brandService.findPage(searchMap, page, size);
        PageResult pageResult = new PageResult(pageList.getTotal(), pageList.getResult());
        return new Result<>(true, StatusCode.OK, "查询成功", pageResult);
    }

    /**
     * 根据分类名称，查询对应有哪些品牌
     * //http://ip:port/brand/category/女士包袋
     * @param categoryName 分类名称
     * @return 返回前端的响应
     */
    @GetMapping("/category/{category}")
    public Result<List<Map<String, Object>>> findByCategoryName(@PathVariable("category") String categoryName) {
        List<Map<String, Object>> list = brandService.findByCategoryName(categoryName);
        return new Result<>(true, StatusCode.OK, "查询成功", list);
    }

}
