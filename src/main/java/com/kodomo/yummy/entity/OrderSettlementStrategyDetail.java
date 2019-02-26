package com.kodomo.yummy.entity;

import com.kodomo.yummy.entity.entity_enum.OrderSettlementStrategyType;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

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

    double getOrderSettlementStrategyTypeDefaultValue() {
        if (type == null) return 0;
        return type.getDefaultValue();
    }

    boolean isValid(Restaurant restaurant) {
        return type != null && type.isValid(key, restaurant);
    }

    public String getText() {
        if (type == null) return "-";
        return type.getText() + ":" + key + ",比率:" + rate;
    }
}
