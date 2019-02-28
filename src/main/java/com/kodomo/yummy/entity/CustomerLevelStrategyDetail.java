package com.kodomo.yummy.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-27 10:25
 */
@Entity
@Getter
@Setter
public class CustomerLevelStrategyDetail {

    @Id
    @Column(name = "customer_level_strategy_detail")
    @GeneratedValue(generator = "generator_cld")
    @GenericGenerator(name = "generator_cld", strategy = "native")
    private Integer customerLevelStrategyDetailId;
    @Column(name = "consumption_amount", nullable = false)
    private Double consumptionAmount;//消费金额
    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false, name = "discount_rate")
    private Double discountRate;//折扣率
}
