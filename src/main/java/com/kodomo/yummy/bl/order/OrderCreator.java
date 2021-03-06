package com.kodomo.yummy.bl.order;

import com.kodomo.yummy.controller.vo.OrderVo;
import com.kodomo.yummy.dao.*;
import com.kodomo.yummy.entity.*;
import com.kodomo.yummy.entity.customer.Customer;
import com.kodomo.yummy.entity.customer.CustomerLevelStrategy;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.entity.order.Order;
import com.kodomo.yummy.entity.order.OrderDetail;
import com.kodomo.yummy.entity.order.OrderSettlementStrategy;
import com.kodomo.yummy.entity.restaurant.Offering;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import com.kodomo.yummy.entity.restaurant.RestaurantStrategy;
import com.kodomo.yummy.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-23 16:00
 */
@Component
public class OrderCreator {

    private final CustomerDao customerDao;
    private final RestaurantDao restaurantDao;
    private final OrderSettlementStrategyDao orderSettlementStrategyDao;
    private final OfferingDao offeringDao;
    private final OrderDao orderDao;
    private final CustomerLevelStrategyDao customerLevelStrategyDao;

    @Autowired
    public OrderCreator(CustomerDao customerDao, RestaurantDao restaurantDao, OrderSettlementStrategyDao orderSettlementStrategyDao, OfferingDao offeringDao, OrderDao orderDao, CustomerLevelStrategyDao customerLevelStrategyDao) {
        this.customerDao = customerDao;
        this.restaurantDao = restaurantDao;
        this.orderSettlementStrategyDao = orderSettlementStrategyDao;
        this.offeringDao = offeringDao;
        this.orderDao = orderDao;
        this.customerLevelStrategyDao = customerLevelStrategyDao;
    }

    /**
     * 创建新的订单, 检查参数合法性
     *
     * @param email
     * @param vo
     * @return
     */
    Order createNewOrder(String email, OrderVo vo) throws ParamErrorException, UserNotExistsException, UnupdatableException, RestaurantHasClosedException, ExceedRemainException {
        if (email == null || vo == null) {
            throw new ParamErrorException();
        }

        List<String> errorFields = new ArrayList<>();
        //customer
        Customer customer = customerDao.find(email);
        if (customer == null) {
            throw new UserNotExistsException("顾客不存在");
        }
        if (customer.getState() != UserState.ACTIVATED) {
            throw new UnupdatableException(customer.getState());
        }
        //restaurant
        Restaurant restaurant = restaurantDao.find(vo.getRestaurantId());
        if (restaurant == null) {
            errorFields.add("餐厅id");
        }
        if (restaurant.getState() != UserState.ACTIVATED) {
            throw new UnupdatableException(restaurant.getState());
        }
        //location
        Location location = customer.getLocationById(vo.getLocationId());
        if (location == null) {
            errorFields.add("位置");
        }

        if (!restaurant.isOpenNow()) {
            //餐厅已经关门
            throw new RestaurantHasClosedException();
        }

        //details
        List<OrderDetail> details = new ArrayList<>();
        if (vo.getDetails().size() == 0) {
            errorFields.add("所选商品");
        }
        List<String> exceededOfferingNames = new ArrayList<>();
        vo.getDetails().forEach(detail -> {
            if (detail.getQuantity() == null || detail.getQuantity() <= 0) {
                errorFields.add("商品数量");
                return;
            }
            Offering offering = offeringDao.find(detail.getOfferingId());
            if (offering == null) {
                errorFields.add("商品id");
                return;
            }
            if (offering.getRemainingNumber() != null && offering.getRemainingNumber() - detail.getQuantity() < 0) {
                exceededOfferingNames.add(offering.getName());
            }
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setQuantity(detail.getQuantity());
            orderDetail.setOffering(offering);
            details.add(orderDetail);
        });

        //异常处理
        if (!exceededOfferingNames.isEmpty()) {
            throw new ExceedRemainException(exceededOfferingNames);
        }
        if (!errorFields.isEmpty()) {
            throw new ParamErrorException(errorFields);
        }

        //orderSettlementStrategy
        OrderSettlementStrategy settlementStrategy = orderSettlementStrategyDao.getCurrentOrderSettlementStrategy();
        //customerLevelStrategy
        CustomerLevelStrategy customerLevelStrategy = customerLevelStrategyDao.getCurrentStrategy();
        //restaurantStrategy
        RestaurantStrategy restaurantStrategy = restaurant.getAppliedRestaurantStrategy(details.stream().mapToDouble(OrderDetail::getTotalPrice).sum());

        //生成order
        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setLocation(location);
        order.setDetails(details);
        order.setRestaurantStrategy(restaurantStrategy);
        order.setOrderSettlementStrategy(settlementStrategy);
        order.setCustomerLevelStrategy(customerLevelStrategy);
        //保存到数据库
        order = orderDao.save(order);
        return order;
    }
}
