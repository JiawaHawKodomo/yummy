package com.kodomo.yummy.controller.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 餐品信息
 *
 * @author Shuaiyu Yao
 * @create 2019-02-21 18:33
 */
@Data
public class OfferingVo {
    private String name;
    private Double price;
    private String note;
    private Integer id;
    private List<Integer> types;
    private Date startTime;
    private Date endTime;
}
