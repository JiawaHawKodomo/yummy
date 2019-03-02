package com.kodomo.yummy.bl.restaurant;

import com.kodomo.yummy.bl.order.OrderHelper;
import com.kodomo.yummy.controller.vo.OrderStatisticsInfoVo;
import com.kodomo.yummy.controller.vo.RestaurantStatisticsVo;
import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.entity.order.Order;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-27 16:50
 */
@Component
public class RestaurantStatisticsHelper {

    private final RestaurantDao restaurantDao;
    private final OrderHelper orderHelper;

    @Autowired
    public RestaurantStatisticsHelper(RestaurantDao restaurantDao, OrderHelper orderHelper) {
        this.restaurantDao = restaurantDao;
        this.orderHelper = orderHelper;
    }

    List<OrderStatisticsInfoVo> getStatisticsInfos(Integer rid) {
        Restaurant restaurant = restaurantDao.find(rid);
        if (restaurant == null) {
            return new ArrayList<>();
        }

        return orderHelper.mapToStatisticsVo(restaurant.getOrders());
    }

    List<Order> getOrdersByTimeOfCustomer(Integer rid, String time, String format) {
        Restaurant restaurant = restaurantDao.find(rid);
        if (restaurant == null) {
            return new ArrayList<>();
        }

        return orderHelper.filterOrderByTime(restaurant.getOrders(), time, format);
    }

    List<RestaurantStatisticsVo> getRestaurantStatisticsInfo() {
        return restaurantDao.findAll().stream()
                .map(restaurant -> {
                    RestaurantStatisticsVo vo = new RestaurantStatisticsVo();
                    vo.setRestaurantId(restaurant.getRestaurantId());
                    vo.setCreateTime(restaurant.getRegisterTime());
                    vo.setCity(restaurant.getCity());
                    vo.setLat(restaurant.getLat());
                    vo.setLng(restaurant.getLng());
                    vo.setName(restaurant.getName());
                    return vo;
                }).collect(Collectors.toList());
    }
}
