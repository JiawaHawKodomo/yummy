package com.kodomo.yummy.controller.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-23 13:59
 */
@Data
public class OrderVo {
    private Integer restaurantId;
    private List<OrderDetailVo> details;
    private Integer locationId;
}
