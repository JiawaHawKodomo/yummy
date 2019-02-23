package com.kodomo.yummy.entity;

import com.kodomo.yummy.config.StaticConfig;
import com.kodomo.yummy.entity.entity_enum.OrderState;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-13 14:41
 */
@Getter
@Setter
@Entity
@Table(name = "order_info")
public class Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(generator = "generator_odr")
    @GenericGenerator(name = "generator_odr", strategy = "native")
    private Integer orderId;

    @Column(nullable = false, insertable = false, columnDefinition = "integer default 0")
    private OrderState state;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "customer_email")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Set<OrderDetail> details;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH}, mappedBy = "order")
    private Set<OrderLog> logs;

    /**
     * 该订单生效的满减策略
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "restaurant_strategy_id")
    private RestaurantStrategy restaurantStrategy;

    /**
     * 该订单生效的结算策略
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "order_settlement_strategy_id")
    private OrderSettlementStrategy orderSettlementStrategy;

    public double getDiscount() {
        if (restaurantStrategy == null || restaurantStrategy.getDiscount() == null) return 0;
        return restaurantStrategy.getDiscount();
    }

    public double getTotalPriceBeforeDiscount() {
        if (details == null) return 0;
        return details.stream().mapToDouble(OrderDetail::getTotalPrice).sum();
    }

    public double getTotalPriceAfterDiscount() {
        double tmp = getTotalPriceBeforeDiscount() - getDiscount();
        double min = StaticConfig.getMinPayment();
        return tmp < min ? min : tmp;
    }

    public List<OrderDetail> getDetailsByPrice() {
        if (getDetails() == null) return new ArrayList<>();
        List<OrderDetail> result = new ArrayList<>(getDetails());
        result.sort((a, b) -> (int) b.getTotalPrice() - (int) a.getTotalPrice());
        return result;
    }

    /**
     * 获取该订单最近更新的时间
     *
     * @return 如果没有记录则返回0时间戳
     */
    @NotNull
    public Date getTheLastUpdateTime() {
        if (getLogs() == null) return new Date(0);
        return getLogs().stream().map(OrderLog::getDate).reduce((a, b) -> {
            if (a == null && b == null) return new Date(0);
            if (a == null) return b;
            if (b == null) return a;
            return a.getTime() < b.getTime() ? b : a;
        }).orElse(new Date(0));
    }

    public boolean isUnpaid() {
        return getState() == OrderState.UNPAID;
    }

    public boolean isOngoing() {
        return getState() == OrderState.ONGOING;
    }
}
