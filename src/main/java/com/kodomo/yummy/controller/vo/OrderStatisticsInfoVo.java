package com.kodomo.yummy.controller.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-26 15:14
 */
@Data
public class OrderStatisticsInfoVo {

    private Integer orderId;
    private Date time;
    private Double money;
    private Integer restaurantId;
    private String restaurantName;
    private String customerEmail;
    private Double moneyToRestaurant;
}
