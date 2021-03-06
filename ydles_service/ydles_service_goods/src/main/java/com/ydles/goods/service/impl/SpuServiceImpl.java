package com.ydles.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.ydles.goods.dao.*;
import com.ydles.goods.pojo.*;
import com.ydles.goods.service.SpuService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ydles.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    /**
     * 查询全部列表
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 根据ID查询
     * @param id 要查询的spu id
     * @return
     */
    @Override
    public Goods findById(String id){
        // 查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        // 查询sku list
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", id);
        List<Sku> skuList = skuMapper.selectByExample(example);
        // 封装goods返回
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        return goods;
    }


    /**
     * 增加
     * @param goods
     */
    @Override
    public void add(Goods goods){

        Spu spu = goods.getSpu();
        long spuId = idWorker.nextId();
        spu.setId(spuId+"");
        // 暂未上架
        spu.setIsMarketable("0");
        // 启用规格
        spu.setIsEnableSpec("1");
        // 没有删除
        spu.setIsDelete("0");
        // 未审核
        spu.setStatus("0");
        // 增加spu
        spuMapper.insertSelective(spu);

        // 增加sku list
        saveSkuList(goods);
    }

    /**
     * 插入skuList
     * @param goods 商品
     */
    private void saveSkuList(Goods goods) {
        List<Sku> skuList = goods.getSkuList();
        Spu spu = goods.getSpu();
        String spuName = spu.getName();
        Integer category3Id = spu.getCategory3Id();
        Category category = categoryMapper.selectByPrimaryKey(category3Id);

        Integer brandId = spu.getBrandId();
        Brand brand = brandMapper.selectByPrimaryKey(brandId);
        for (Sku sku : skuList) {
            // 设置id
            long skuId = idWorker.nextId();
            sku.setId(skuId + "");

            // 设置name
            String spec = sku.getSpec();
            if(spec == null || spec.length() == 0){
                spec = "{}";
            }
            StringBuilder sb = new StringBuilder();
            sb.append(spuName);
            Map<String, String> map = JSON.parseObject(spec, Map.class);
            for (String value : map.values()) {
                sb.append(" ").append(value);
            }
            sku.setName(new String(sb));

            // 设置时间
            sku.setCreateTime(new Date());
            sku.setUpdateTime(new Date());

            sku.setSpuId(spu.getId());
            // 设置类目
            sku.setCategoryId(category3Id);
            sku.setCategoryName(category.getName());

            sku.setBrandName(brand.getName());

            sku.setSaleNum(0);
            sku.setCommentNum(0);
            sku.setStatus("1");

            skuMapper.insertSelective(sku);
        }

        // 品牌和分类关系表 也要进行更新
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setCategoryId(category3Id);
        categoryBrand.setBrandId(brand.getId());
        int cnt = categoryBrandMapper.selectCount(categoryBrand);
        // 如果没有关联，就要新增
        if(cnt <= 0){
            categoryBrandMapper.insert(categoryBrand);
        }
    }


    /**
     * 修改
     * @param goods
     */
    @Override
    public void update(Goods goods){
        // 修改spu
        Spu spu = goods.getSpu();
        spuMapper.updateByPrimaryKeySelective(spu);
        // 删除关联的sku
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",spu.getId());
        skuMapper.deleteByExample(example);
        // 插入修改带来的sku
        saveSkuList(goods);
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id){
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu == null){
            throw new RuntimeException("当前商品不存在!");
        }
        // 已下架的商品才可以删除
        if(!"0".equals(spu.getIsMarketable())){
            throw new RuntimeException("当前商品正在上架,不能删除");
        }
        spu.setIsDelete("1");
        // 逻辑删除
        spuMapper.updateByPrimaryKeySelective(spu);

    }


    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @Override
    public List<Spu> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Spu> findPage(int page, int size){
        PageHelper.startPage(page,size);
        return (Page<Spu>)spuMapper.selectAll();
    }

    /**
     * 条件+分页查询
     * @param searchMap 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public Page<Spu> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Spu>)spuMapper.selectByExample(example);
    }

    /**
     * 构建查询对象
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 主键
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andEqualTo("id",searchMap.get("id"));
           	}
            // 货号
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andEqualTo("sn",searchMap.get("sn"));
           	}
            // SPU名
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
           	}
            // 副标题
            if(searchMap.get("caption")!=null && !"".equals(searchMap.get("caption"))){
                criteria.andLike("caption","%"+searchMap.get("caption")+"%");
           	}
            // 图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
           	}
            // 图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
           	}
            // 售后服务
            if(searchMap.get("saleService")!=null && !"".equals(searchMap.get("saleService"))){
                criteria.andLike("saleService","%"+searchMap.get("saleService")+"%");
           	}
            // 介绍
            if(searchMap.get("introduction")!=null && !"".equals(searchMap.get("introduction"))){
                criteria.andLike("introduction","%"+searchMap.get("introduction")+"%");
           	}
            // 规格列表
            if(searchMap.get("specItems")!=null && !"".equals(searchMap.get("specItems"))){
                criteria.andLike("specItems","%"+searchMap.get("specItems")+"%");
           	}
            // 参数列表
            if(searchMap.get("paraItems")!=null && !"".equals(searchMap.get("paraItems"))){
                criteria.andLike("paraItems","%"+searchMap.get("paraItems")+"%");
           	}
            // 是否上架
            if(searchMap.get("isMarketable")!=null && !"".equals(searchMap.get("isMarketable"))){
                criteria.andEqualTo("isMarketable",searchMap.get("isMarketable"));
           	}
            // 是否启用规格
            if(searchMap.get("isEnableSpec")!=null && !"".equals(searchMap.get("isEnableSpec"))){
                criteria.andEqualTo("isEnableSpec", searchMap.get("isEnableSpec"));
           	}
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andEqualTo("isDelete",searchMap.get("isDelete"));
           	}
            // 审核状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status",searchMap.get("status"));
           	}

            // 品牌ID
            if(searchMap.get("brandId")!=null ){
                criteria.andEqualTo("brandId",searchMap.get("brandId"));
            }
            // 一级分类
            if(searchMap.get("category1Id")!=null ){
                criteria.andEqualTo("category1Id",searchMap.get("category1Id"));
            }
            // 二级分类
            if(searchMap.get("category2Id")!=null ){
                criteria.andEqualTo("category2Id",searchMap.get("category2Id"));
            }
            // 三级分类
            if(searchMap.get("category3Id")!=null ){
                criteria.andEqualTo("category3Id",searchMap.get("category3Id"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
            // 运费模板id
            if(searchMap.get("freightId")!=null ){
                criteria.andEqualTo("freightId",searchMap.get("freightId"));
            }
            // 销量
            if(searchMap.get("saleNum")!=null ){
                criteria.andEqualTo("saleNum",searchMap.get("saleNum"));
            }
            // 评论数
            if(searchMap.get("commentNum")!=null ){
                criteria.andEqualTo("commentNum",searchMap.get("commentNum"));
            }

        }
        return example;
    }

    @Override
    public void audit(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu == null){
            throw new RuntimeException("当前商品不存在!");
        }
        // 校验商品是否是被删除的商品
        if("1".equals(spu.getIsDelete())){
            throw new RuntimeException("当前商品已删除");
        }
        // 如果未审核则标记为审核
        spu.setStatus("1");
        // 标记为上架
        spu.setIsMarketable("1");

        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void pull(String id) {
        // 校验商品是否是被删除的商品
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu == null){
            throw new RuntimeException("当前商品不存在!");
        }
        // 校验商品是否是被删除的商品
        if("1".equals(spu.getIsDelete())){
            throw new RuntimeException("当前商品已删除");
        }
        // 未删除的话则修改上架状态为0
        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void put(String id) {
        // 校验商品是否是被删除的商品
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu == null){
            throw new RuntimeException("当前商品不存在!");
        }
        // 上架商品，需要审核状态为1
        if(!"1".equals(spu.getStatus())){
            throw new RuntimeException("当前商品未通过审核!");
        }
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void resotre(String id) {
        // 校验商品是否是被删除的商品
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu == null){
            throw new RuntimeException("当前商品不存在!");
        }
        // 标记为 未删除
        spu.setIsDelete("0");
        // 标记为 未审核
        spu.setStatus("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void realDelete(String id) {
        // 校验商品是否是被删除的商品
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu == null){
            throw new RuntimeException("当前商品不存在!");
        }
        // 必须逻辑删除的才可以物理删除
        if(!"1".equals(spu.getIsDelete())){
            throw new RuntimeException("当前商品并未逻辑删除!");
        }
        spuMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Spu findSpuById(String id) {
        return spuMapper.selectByPrimaryKey(id);
    }
}
