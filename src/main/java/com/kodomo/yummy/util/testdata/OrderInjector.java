package com.kodomo.yummy.util.testdata;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.controller.vo.OrderDetailVo;
import com.kodomo.yummy.controller.vo.OrderVo;
import com.kodomo.yummy.entity.customer.Customer;
import com.kodomo.yummy.entity.order.Order;
import com.kodomo.yummy.entity.restaurant.Offering;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import com.kodomo.yummy.exceptions.*;
import com.kodomo.yummy.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-06-05 17:14
 */
@Slf4j
@Component
public class OrderInjector {

    @Value("${testdata.order.maxOfferingTypeNum}")
    private Integer maxOfferingTypeNum;
    @Value("${testdata.order.endingTime}")
    private String endTime;
    @Value("${testdata.order.maxDailyOrderNum}")
    private Integer maxDailyOrderNum;
    @Value("${testdata.order.minDeliveryTime}")
    private Integer minDeliveryTime;
    @Value("${testdata.order.maxDeliveryTime}")
    private Integer maxDeliveryTime;
    @Value("${testdata.customer.rechargeAmount}")
    private Double rechargeAmount;

    private final CustomerBlService customerBlService;
    private final RandomHelper randomHelper;
    private final OrderBlService orderBlService;

    @Autowired
    public OrderInjector(CustomerBlService customerBlService, RandomHelper randomHelper, OrderBlService orderBlService) {
        this.customerBlService = customerBlService;
        this.randomHelper = randomHelper;
        this.orderBlService = orderBlService;
    }

    int injectOrders(List<Customer> customers) {
        int num = 0;
        for (Customer customer : customers) {
            num += orderForCustomer(customer);
        }
        return num;
    }

    private int orderForCustomer(Customer customer) {
        int num = 0;
        try {
            Integer locationId = customer.getLocations().get(0).getLocationId();
            List<Restaurant> restaurants = customerBlService.getRestaurantWithinDistributionDistance(customer.getEmail(),
                    locationId);

            List<Date> orderDates = randomOrderDateForCustomer(customer);
            for (Date date : orderDates) {
                Collections.shuffle(restaurants);
                for (Restaurant restaurant : restaurants) {
                    if (restaurant.getRegisterTime().before(date)) {
                        order(customer, restaurant, date, locationId);
                        num++;
                        break;
                    }
                }

            }
        } catch (ParamErrorException | NoSuchAttributeException | UserNotExistsException e) {
            log.error("未知错误");
            e.printStackTrace();
        } finally {
            return num;
        }
    }

    private void recharge(Customer customer, Date date) {
        try {
            customerBlService.recharge(customer.getEmail(), rechargeAmount, date);
        } catch (ParamErrorException | UserNotExistsException | UnupdatableException e) {
            log.error("未知错误");
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void order(Customer customer, Restaurant restaurant, Date date, Integer locationId) {

        OrderVo orderVo = new OrderVo();
        orderVo.setLocationId(locationId);
        orderVo.setRestaurantId(restaurant.getRestaurantId());
        orderVo.setDetails(selectOffering(restaurant));
        try {
            //下单
            Order order = orderBlService.createNewOrder(customer.getEmail(), orderVo, date);

            //付款
            Date payDate = new Date();
            payDate.setTime(date.getTime() + 10 * 1000L);
            while (true) {
                try {
                    orderBlService.payOrder(customer.getEmail(), customer.getPassword(), order.getOrderId(), payDate);
                    break;
                } catch (LackOfBalanceException e) {
                    //如果余额不足就充值直到余额足够
                    recharge(customer, payDate);
                }
            }
            //结算
            orderBlService.customerConfirmOrder(customer.getEmail(), order.getOrderId(), randomArrivalTime(payDate));
        } catch (RestaurantHasClosedException | ExceedRemainException | OrderTimeOutException ignored) {
        } catch (PasswordErrorException | ParamErrorException | UserNotExistsException | UnupdatableException | NoSuchAttributeException e) {
            e.printStackTrace();
        }

    }

    private Date randomArrivalTime(Date startTime) {
        Date result = new Date();
        result.setTime(startTime.getTime() +
                minDeliveryTime * 60 * 1000 + randomHelper.randomInt((maxDeliveryTime - minDeliveryTime) * 60 * 1000));
        return result;
    }

    private List<OrderDetailVo> selectOffering(Restaurant restaurant) {
        List<OrderDetailVo> vos = new ArrayList<>();
        int offeringTypeNumber = randomHelper.randomInt(maxOfferingTypeNum);
        if (offeringTypeNumber == 0) offeringTypeNumber = 1;

        List<Offering> offerings = new ArrayList<>(restaurant.getOfferings());
        Collections.shuffle(offerings);

        for (int i = 0; i < offeringTypeNumber; i++) {
            Offering offering = offerings.get(i);
            OrderDetailVo detailVo = new OrderDetailVo();
            detailVo.setQuantity(1);
            detailVo.setOfferingId(offering.getOfferingId());
            vos.add(detailVo);
        }
        return vos;
    }

    private List<Date> randomOrderDateForCustomer(Customer customer) {
        List<Date> dates = new ArrayList<>();

        long dayTime = 24 * 3600 * 1000L;
        Date date = new Date();
        long registerTime = customer.getRegisterTime().getTime();
        date.setTime(registerTime - (registerTime % dayTime) + dayTime);

        try {
            Date endTime = Utility.parseTime(this.endTime);
            while (date.before(endTime)) {
                for (int i = 0; i < maxDailyOrderNum; i++) {
                    Random random = new Random();
                    if (randomHelper.randomBoolean()) {
                        Date newDate = new Date(date.getTime());
                        newDate.setTime((long) (date.getTime() + 24 * 3600 * 1000 * random.nextDouble()));
                        dates.add(newDate);
                    }
                }
                date.setTime(date.getTime() + dayTime);
            }
            return dates;
        } catch (ParseException e) {
            log.error("订单截止时间错误");
            System.exit(0);
        }

        return dates;
    }
}
