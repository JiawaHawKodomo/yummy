package com.kodomo.yummy.controller.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-28 10:17
 */
@Data
public class CustomerStatisticsVo {

    private String email;
    private Date registerTime;
    private Double consumptionAmount;
    private Integer orderQuantity;
    private Integer level;
}
