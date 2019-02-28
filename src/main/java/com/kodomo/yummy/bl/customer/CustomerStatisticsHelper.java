package com.kodomo.yummy.bl.customer;

import com.kodomo.yummy.bl.order.OrderHelper;
import com.kodomo.yummy.controller.vo.CustomerRechargeStatisticsVo;
import com.kodomo.yummy.controller.vo.CustomerStatisticsVo;
import com.kodomo.yummy.controller.vo.OrderStatisticsInfoVo;
import com.kodomo.yummy.dao.CustomerDao;
import com.kodomo.yummy.dao.CustomerRechargeLogDao;
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

    private final OrderHelper orderHelper;
    private final CustomerDao customerDao;
    private final CustomerRechargeLogDao customerRechargeLogDao;

    @Autowired
    public CustomerStatisticsHelper(CustomerDao customerDao, OrderHelper orderHelper, CustomerRechargeLogDao customerRechargeLogDao) {
        this.customerDao = customerDao;
        this.orderHelper = orderHelper;
        this.customerRechargeLogDao = customerRechargeLogDao;
    }

    List<OrderStatisticsInfoVo> getStatisticsInfos(String email) throws UserNotExistsException {
        Customer customer = customerDao.find(email);
        if (customer == null) {
            throw new UserNotExistsException();
        }

        return orderHelper.mapToStatisticsVo(customer.getOrders());
    }

    List<Order> getOrdersByRestaurantOfCustomer(String email, Integer rid) throws UserNotExistsException {
        Customer customer = customerDao.find(email);
        if (customer == null) {
            throw new UserNotExistsException();
        }

        return orderHelper.filterOrderByRestaurant(customer.getOrders(), rid);
    }

    List<Order> getOrdersByTimeOfCustomer(String email, String time, String timeFormat) throws UserNotExistsException {
        Customer customer = customerDao.find(email);
        if (customer == null) {
            throw new UserNotExistsException();
        }

        return orderHelper.filterOrderByTime(customer.getOrders(), time, timeFormat);
    }

    List<CustomerStatisticsVo> getCustomerStatisticsVo() {
        return customerDao.findAll().stream()
                .map(customer -> {
                    CustomerStatisticsVo vo = new CustomerStatisticsVo();
                    vo.setEmail(customer.getEmail());
                    vo.setRegisterTime(customer.getRegisterTime());
                    vo.setConsumptionAmount(customer.getTotalConsumptionAmount());
                    vo.setOrderQuantity(customer.getDoneOrders().size());
                    vo.setLevel(customer.getLevel());
                    return vo;
                }).collect(Collectors.toList());
    }

    List<CustomerRechargeStatisticsVo> getCustomerRechargeStatisticsVo() {
        return customerRechargeLogDao.findAll().stream()
                .map(customerRechargeLog -> {
                    CustomerRechargeStatisticsVo vo = new CustomerRechargeStatisticsVo();
                    vo.setTime(customerRechargeLog.getTime());
                    vo.setAmount(customerRechargeLog.getAmount());
                    return vo;
                }).collect(Collectors.toList());
    }
}
