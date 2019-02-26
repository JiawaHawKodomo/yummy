package com.kodomo.yummy.bl.customer;

import com.kodomo.yummy.controller.vo.OrderStatisticsInfoVo;
import com.kodomo.yummy.dao.CustomerDao;
import com.kodomo.yummy.entity.Customer;
import com.kodomo.yummy.entity.Order;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-26 15:19
 */
@Component
public class CustomerStatisticsHelper {

    private final CustomerDao customerDao;

    @Autowired
    public CustomerStatisticsHelper(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    List<OrderStatisticsInfoVo> getStatisticsInfos(String email) throws UserNotExistsException {
        Customer customer = customerDao.find(email);
        if (customer == null) {
            throw new UserNotExistsException();
        }

        return customer.getOrders().stream()
                .filter(order -> !order.isCanceled())
                .map(order -> {
                    OrderStatisticsInfoVo vo = new OrderStatisticsInfoVo();
                    vo.setOrderId(order.getOrderId());
                    vo.setTime(order.getCreateTime());
                    vo.setMoney(order.getTotalPriceAfterDiscount());
                    vo.setRestaurantId(order.getRestaurantId());
                    vo.setRestaurantName(order.getRestaurantName());
                    return vo;
                }).collect(Collectors.toList());
    }

    List<Order> getOrdersByRestaurantOfCustomer(String email, Integer rid) throws UserNotExistsException {
        Customer customer = customerDao.find(email);
        if (customer == null) {
            throw new UserNotExistsException();
        }

        return customer.getOrders().stream()
                .filter(order -> Objects.equals(rid, order.getRestaurantId()))
                .filter(Order::isDone)
                .collect(Collectors.toList());
    }

    public List<Order> getOrdersByTimeOfCustomer(String email, String time, String timeFormat) throws UserNotExistsException {
        Customer customer = customerDao.find(email);
        if (customer == null) {
            throw new UserNotExistsException();
        }

        return customer.getOrders().stream()
                .filter(order -> Objects.equals(time, new SimpleDateFormat(timeFormat).format(order.getCreateTime())))
                .filter(Order::isDone)
                .collect(Collectors.toList());
    }
}
