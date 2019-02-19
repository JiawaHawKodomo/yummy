package com.kodomo.yummy.entity;

import com.kodomo.yummy.entity.entity_enum.OrderSettlementStrategyType;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-19 11:53
 */
@Data
@Entity
@Table(name = "order_settlement_strategy_detail")
public class OrderSettlementStrategyDetail {

    @Id
    @Column(name = "order_settlement_strategy_detail_id")
    @GeneratedValue(generator = "generator_osd")
    @GenericGenerator(name = "generator_osd", strategy = "native")
    private Integer orderSettlementStrategyDetailId;
    @Column(nullable = false)
    private OrderSettlementStrategyType type;
    @Column(nullable = false, name = "strategy_key")
    private String key;
    @Column(nullable = false)
    private Double rate;

    public double getDefaultRateValue() {
        if (type == null) return 1;
        return type.getDefaultValue();
    }

    public boolean isValid(Restaurant restaurant) {
        return type != null && type.isValid(key, restaurant);
    }
}
