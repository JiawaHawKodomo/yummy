package com.kodomo.yummy.controller.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-28 15:34
 */
@Data
public class CustomerRechargeStatisticsVo {

    private Date time;
    private Double amount;

}
