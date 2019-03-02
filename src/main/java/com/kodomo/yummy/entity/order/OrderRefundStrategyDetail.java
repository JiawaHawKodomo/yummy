package com.kodomo.yummy.entity.order;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-25 12:47
 */
@Entity
@Getter
@Setter
@Table(name = "order_refund_strategy_detail")
public class OrderRefundStrategyDetail {

    @Id
    @Column(name = "order_refund_strategy_detail_id")
    @GeneratedValue(generator = "generator_ord")
    @GenericGenerator(name = "generator_ord", strategy = "native")
    private Integer orderRefundStrategyDetailId;

    @Column(name = "more_than_or_equals", nullable = false)
    private Integer moreThanOrEquals;//大于等于时间
    @Column(name = "less_than", nullable = false)
    private Integer lessThan;//小于时间
    @Column(name = "refundRate", nullable = false)
    private Double refundRate;//退款比例
}
