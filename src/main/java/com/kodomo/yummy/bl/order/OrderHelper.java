package com.kodomo.yummy.bl.order;

import com.kodomo.yummy.controller.vo.OrderStatisticsInfoVo;
import com.kodomo.yummy.entity.order.Order;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-27 16:57
 */
@Component
public class OrderHelper {

    public List<OrderStatisticsInfoVo> mapToStatisticsVo(Collection<Order> orders) {
        return orders.stream()
                .filter(order -> !order.isCanceled())
                .map(order -> {
                    OrderStatisticsInfoVo vo = new OrderStatisticsInfoVo();
                    vo.setOrderId(order.getOrderId());
                    vo.setTime(order.getCreateTime());
                    vo.setMoney(order.getTotalPriceAfterDiscount());
                    vo.setRestaurantId(order.getRestaurantId());
                    vo.setRestaurantName(order.getRestaurantName());
                    vo.setMoneyToRestaurant(order.getFinalMoneyToRestaurant());
                    vo.setCustomerEmail(order.getCustomerEmail());
                    return vo;
                }).collect(Collectors.toList());
    }


    public List<Order> filterOrderByTime(Collection<Order> orders, String time, String timeFormat) {
        if (orders == null) return new ArrayList<>();
        return orders.stream()
                .filter(order -> Objects.equals(time, new SimpleDateFormat(timeFormat).format(order.getCreateTime())))
                .filter(Order::isDone)
                .collect(Collectors.toList());
    }

    public List<Order> filterOrderByRestaurant(Collection<Order> orders, Integer restaurantId) {
        if (orders == null) return new ArrayList<>();
        return orders.stream()
                .filter(order -> Objects.equals(restaurantId, order.getRestaurantId()))
                .filter(Order::isDone)
                .collect(Collectors.toList());
    }
}
