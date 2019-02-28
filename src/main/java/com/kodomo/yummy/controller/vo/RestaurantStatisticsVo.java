package com.kodomo.yummy.controller.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-27 20:03
 */
@Data
public class RestaurantStatisticsVo {

    private Integer restaurantId;
    private String city;
    private Date createTime;
    private Double lat;
    private Double lng;
    private String name;
}
