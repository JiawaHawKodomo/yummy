package com.kodomo.yummy.entity;

import com.kodomo.yummy.config.StaticConfig;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * 订单退款策略
 *
 * @author Shuaiyu Yao
 * @create 2019-02-25 12:46
 */
@Getter
@Setter
@Entity
@Table(name = "order_refund_strategy")
public class OrderRefundStrategy {

    @Id
    @Column(name = "order_refund_strategy")
    @GeneratedValue(generator = "generator_ors")
    @GenericGenerator(name = "generator_ors", strategy = "native")
    private Integer orderRefundStrategyId;
    @Column(name = "start_time", columnDefinition = "timestamp default now()")
    private Date startTime;
    @Column(name = "end_time", columnDefinition = "timestamp default '2037-01-01'", insertable = false)
    private Date endTime;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "strategy_id")
    private Set<OrderRefundStrategyDetail> details;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Manager manager;//制定人

    double getRate(Date ongoingTime) {
        if (getDetails() == null) return StaticConfig.getOrderRefundDefaultRate();
        int minuit = (int) (new Date().getTime() - ongoingTime.getTime()) / 1000 / 60;
        for (OrderRefundStrategyDetail detail : getDetails()) {
            if (detail.getMoreThanOrEquals() == null || detail.getLessThan() == null || detail.getRefundRate() == null)
                continue;
            if (detail.getMoreThanOrEquals() <= minuit || detail.getLessThan() > minuit) {
                return detail.getRefundRate();
            }
        }
        return StaticConfig.getOrderRefundDefaultRate();
    }
}
