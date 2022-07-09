package com.ydles.goods.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Scz
 * @date 2022/4/12 19:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_category_brand")
public class CategoryBrand implements Serializable {
    @Id
    private Integer categoryId;
    @Id
    private Integer brandId;
}
