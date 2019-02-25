package com.kodomo.yummy.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 订单结算规则
 *
 * @author Shuaiyu Yao
 * @create 2019-02-19 11:11
 */
@Getter
@Setter
@Entity
@Table(name = "order_settlement_strategy")
public class OrderSettlementStrategy {

    @Id
    @Column(name = "order_settlement_strategy_id")
    @GeneratedValue(generator = "generator_oss")
    @GenericGenerator(name = "generator_oss", strategy = "native")
    private Integer orderSettlementStrategyId;
    @Column(name = "start_time", columnDefinition = "timestamp default now()")
    private Date startTime;
    @Column(name = "end_time", columnDefinition = "timestamp default '2037-01-01'", insertable = false)
    private Date endTime;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "strategy_id")
    private Set<OrderSettlementStrategyDetail> details;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Manager manager;//制定人

    /**
     * 根据餐厅的情况计算最后的折算率
     *
     * @param restaurant 餐厅
     * @return 最终的折算率
     */
    public double getFinalRate(Restaurant restaurant) {
        double result = 1.0;
        result -= details.stream()
                .collect(Collectors.groupingBy(OrderSettlementStrategyDetail::getType, Collectors.toList())).values()
                .stream()
                .mapToDouble(l -> l.stream().mapToDouble(
                        d -> d.isValid(restaurant) ? d.getRate() : d.getOrderSettlementStrategyTypeDefaultValue()
                        ).min().orElse(0)
                ).sum();
        //计算
        return result;
    }

    @Override
    public String toString() {
        return "OrderSettlementStrategy{" +
                "orderSettlementStrategyId=" + orderSettlementStrategyId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", details=" + details +
                ", manager=" + manager.getManagerId() +
                '}';
    }
}
