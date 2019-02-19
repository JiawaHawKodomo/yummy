package com.kodomo.yummy.entity.util.order;

import com.kodomo.yummy.entity.Restaurant;

/**
 * 计算Strategy的折扣率
 *
 * @author Shuaiyu Yao
 * @create 2019-02-19 12:31
 */
@FunctionalInterface
public interface OrderStrategyCalculator {

    /**
     * 判断该策略是否适用于该餐厅
     *
     * @param key        detail的关键字
     * @param restaurant 餐厅
     * @return boolean
     */
    boolean isValid(String key, Restaurant restaurant);
}
