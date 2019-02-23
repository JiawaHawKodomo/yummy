package com.kodomo.yummy.controller.vo;

import lombok.Data;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-23 15:01
 */
@Data
public class OrderSettlementStrategyVo {

    private Integer type;
    private String key;
    private Double rate;

}
