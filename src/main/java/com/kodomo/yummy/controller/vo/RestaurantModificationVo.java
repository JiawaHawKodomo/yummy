package com.kodomo.yummy.controller.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-28 18:27
 */
@Data
public class RestaurantModificationVo {

    private String name;
    private String telephone;
    private String businessHours;
    private String locationNote;
    private List<String> types;

}
