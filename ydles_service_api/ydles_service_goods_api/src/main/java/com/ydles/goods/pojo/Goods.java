package com.ydles.goods.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * spu 和 skuList 类
 * @author Scz
 * @date 2022/4/12 17:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goods {
    private Spu spu;
    private List<Sku> skuList;
}
