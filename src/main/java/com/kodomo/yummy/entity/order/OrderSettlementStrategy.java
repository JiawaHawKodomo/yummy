package com.kodomo.yummy.entity.order;

import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id")
    private List<OrderSettlementStrategyDetail> details;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Manager manager;//制定人

    /**
     * 根据餐厅的情况计算最后的折算率
     *
     * @param restaurant 餐厅
     * @return 最终的折算率
     */
    double getFinalRate(Restaurant restaurant) {
        double result = 1.0;
        result -= getAppliedDetails(restaurant).stream()
                .mapToDouble(OrderSettlementStrategyDetail::getRate).sum();
        return result;
    }

    /**
     * 根据餐厅的情况筛选最后适用的结算策略条例
     *
     * @param restaurant
     * @return
     */
    List<OrderSettlementStrategyDetail> getAppliedDetails(Restaurant restaurant) {
        return details.stream()
                .collect(Collectors.groupingBy(OrderSettlementStrategyDetail::getType, Collectors.toList())).values()
                .stream()
                .map(l -> l.stream()
                        .filter(d -> d.isValid(restaurant))
                        .reduce((a, b) -> a.getRate() < b.getRate() ? a : b).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
