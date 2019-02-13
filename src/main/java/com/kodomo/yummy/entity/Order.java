package com.kodomo.yummy.entity;

import com.kodomo.yummy.config.StaticConfig;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-13 14:41
 */
@Data
@Entity
@Table(name = "order_info")
public class Order {

    @Id
    @GeneratedValue(generator = "generator_odr")
    @GenericGenerator(name = "generator_odr", strategy = "native")
    private Integer orderId;

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp()")
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_email")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Set<OrderDetail> details;

    /**
     * 该订单生效的满减策略
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "strategy_id")
    private RestaurantStrategy strategy;

    public double getDiscount() {
        if (strategy == null || strategy.getDiscount() == null) return 0;
        return strategy.getDiscount();
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
}
