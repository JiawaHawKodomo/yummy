package com.kodomo.yummy.entity.order;

import com.kodomo.yummy.entity.entity_enum.OrderSettlementStrategyType;
import com.kodomo.yummy.entity.restaurant.Restaurant;
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
