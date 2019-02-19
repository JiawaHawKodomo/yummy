package com.kodomo.yummy.entity.entity_enum;

import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.entity.util.order.OrderStrategyCalculator;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-19 11:50
 */
public enum OrderSettlementStrategyType {

    //城市策略, 检查是否是该地点
    CITY("城市", 0.9, (key, restaurant) -> restaurant.getCity().equals(key) || restaurant.getCity().contains(key)),
    //订单数策略, 检查是否大于该数值
    ORDER_QUANTITY("订单数量", 0.9, ((key, restaurant) -> restaurant.getOrderQuantity() >= Integer.parseInt(key)));

    private String text;
    private OrderStrategyCalculator calculator;
    private double defaultValue;

    OrderSettlementStrategyType(String text, double defaultValue, OrderStrategyCalculator calculator) {
        this.text = text;
        this.defaultValue = defaultValue;
        this.calculator = calculator;
    }

    public String getText() {
        return text;
    }

    public boolean isValid(String key, Restaurant restaurant) {
        return calculator.isValid(key, restaurant);
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public static OrderSettlementStrategyType getByIndex(int index) {
        for (OrderSettlementStrategyType type : values()) {
            if (type.ordinal() == index) {
                return type;
            }
        }
        return null;
    }
}
