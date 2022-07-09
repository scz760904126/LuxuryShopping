package com.ydles.goods.service;

import com.ydles.goods.pojo.Goods;
import com.ydles.goods.pojo.Spu;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface SpuService {

    /***
     * 查询所有
     * @return
     */
    List<Spu> findAll();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Goods findById(String id);

    /***
     * 新增
     * @param goods
     */
    void add(Goods goods);

    /***
     * 修改
     * @param goods
     */
    void update(Goods goods);

    /***
     * 删除
     * @param id
     */
    void delete(String id);

    /***
     * 多条件搜索
     * @param searchMap
     * @return
     */
    List<Spu> findList(Map<String, Object> searchMap);

    /***
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(int page, int size);

    /***
     * 多条件分页查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(Map<String, Object> searchMap, int page, int size);


    void audit(String id);

    void pull(String id);

    void put(String id);

    void resotre(String id);

    void realDelete(String id);

    Spu findSpuById(String id);
}
