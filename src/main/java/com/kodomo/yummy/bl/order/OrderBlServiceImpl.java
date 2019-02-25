package com.kodomo.yummy.bl.order;

import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.controller.vo.OrderRefundStrategyVo;
import com.kodomo.yummy.controller.vo.OrderSettlementStrategyVo;
import com.kodomo.yummy.controller.vo.OrderVo;
import com.kodomo.yummy.dao.*;
import com.kodomo.yummy.entity.*;
import com.kodomo.yummy.entity.entity_enum.OrderState;
import com.kodomo.yummy.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @author Shuaiyu Yao
 * @create 2019-02-19 11:45
 */
@Service
public class OrderBlServiceImpl implements OrderBlService {

    @Value("${yummy-system.pay-time}")
    private Integer maxPayTime;//最长等待支付时间, 单位分钟

    private final OrderDao orderDao;
    private final OrderCreator orderCreator;
    private final OrderLogDao orderLogDao;
    private final CustomerDao customerDao;
    private final OrderLogHelper orderLogHelper;
    private final RestaurantDao restaurantDao;
    private final OrderStrategyHelper orderStrategyHelper;

    @Autowired
    public OrderBlServiceImpl(OrderDao orderDao, OrderCreator orderCreator, OrderLogDao orderLogDao, CustomerDao customerDao, OrderLogHelper orderLogHelper, RestaurantDao restaurantDao, OrderStrategyHelper orderStrategyHelper) {
        this.orderDao = orderDao;
        this.orderCreator = orderCreator;
        this.orderLogDao = orderLogDao;
        this.customerDao = customerDao;
        this.orderLogHelper = orderLogHelper;
        this.restaurantDao = restaurantDao;
        this.orderStrategyHelper = orderStrategyHelper;
    }

    @Override
    public OrderSettlementStrategy getCurrentOrderSettlementStrategy() {
        return orderStrategyHelper.getCurrentOrderSettlementStrategy();
    }

    /**
     * 获取当前订单退款策略
     *
     * @return
     */
    @Override
    public OrderRefundStrategy getCurrentOrderRefundStrategy() {
        return orderStrategyHelper.getCurrentOrderRefundStrategy();
    }

    @Override
    public void saveNewOrderSettlementStrategy(List<OrderSettlementStrategyVo> vos, String managerId) throws ParamErrorException, UserNotExistsException {
        orderStrategyHelper.saveNewOrderSettlementStrategy(vos, managerId);
    }

    /**
     * 保存新的订单退款策略
     *
     * @param vos
     * @param managerId
     * @throws UserNotExistsException 管理员不存在
     */
    @Override
    public void saveNewOrderRefundStrategy(List<OrderRefundStrategyVo> vos, String managerId) throws UserNotExistsException, ParamErrorException {
        orderStrategyHelper.saveNewOrderRefundStrategy(vos, managerId);
    }

    /**
     * 根据id查找Order
     *
     * @param id
     * @return
     */
    @Override
    public Order getOrderById(Integer id) {
        if (id == null) return null;
        return orderDao.findById(id).orElse(null);
    }

    /**
     * 创建新的订单
     *
     * @param email email
     * @param vo    vo
     * @return
     */
    @Override
    public Order createNewOrder(String email, OrderVo vo) throws ParamErrorException, UserNotExistsException, UnupdatableException {
        Order order = orderCreator.createNewOrder(email, vo);

        //生成新的订单记录
        OrderLog orderLog = new OrderLog();
        orderLog.setOrder(order);
        orderLog.setToState(OrderState.UNPAID);
        orderLogDao.save(orderLog);

        return order;
    }

    /**
     * 支付订单
     *
     * @param email   email
     * @param orderId orderId
     * @throws ParamErrorException      参数错误
     * @throws UserNotExistsException   customer不存在
     * @throws NoSuchAttributeException 订单不存在
     * @throws UnupdatableException     订单状态不正确
     * @throws OrderTimeOutException    订单超时
     * @throws LackOfBalanceException   余额不足
     */
    @Override
    public void payOrder(String email, String password, Integer orderId) throws ParamErrorException, UserNotExistsException, NoSuchAttributeException, OrderTimeOutException, LackOfBalanceException, UnupdatableException, PasswordErrorException {
        if (email == null || orderId == null || password == null) {
            throw new ParamErrorException();
        }
        Customer customer = customerDao.find(email);
        if (customer == null) {
            throw new UserNotExistsException();
        }
        if (!password.equals(customer.getPassword())) {
            throw new PasswordErrorException();
        }
        Order order = customer.getOrderByOrderId(orderId);
        if (order == null) {
            throw new NoSuchAttributeException();
        }

        //状态判断
        if (!order.isUnpaid()) {
            throw new UnupdatableException(order.getState());
        }

        //时间判断
        Date createTime = order.getCreateTime();
        if (createTime == null || new Date().getTime() - createTime.getTime() >= maxPayTime * 60 * 1000) {
            //超时
            throw new OrderTimeOutException(createTime);
        }

        //订单支付
        Double amount = order.getTotalPriceAfterDiscount();
        customer.reduceBalance(amount);
        order.setState(OrderState.ONGOING);

        //数据库处理
        //1. 存储customer
        customerDao.save(customer);

        //2.存储订单, 出错卷回
        try {
            orderDao.save(order);
        } catch (RuntimeException e) {
            customer.increaceBalance(amount);
            customerDao.save(customer);
            throw e;
        }

        //3.存储订单日志, 出错卷回
        try {
            orderLogHelper.createOrderLog(order, OrderState.ONGOING);
        } catch (RuntimeException e) {
            order.setState(OrderState.UNPAID);
            orderDao.save(order);
            customer.increaceBalance(amount);
            customerDao.save(customer);
            throw e;
        }
    }

    /**
     * 确认订单
     *
     * @param email   email
     * @param orderId orderId
     * @throws ParamErrorException      参数不正确
     * @throws UserNotExistsException   customer不存在
     * @throws NoSuchAttributeException 订单不存在
     * @throws UnupdatableException     订单状态不正确
     */
    @Override
    public void confirmOrder(String email, Integer orderId) throws ParamErrorException, UserNotExistsException, NoSuchAttributeException, UnupdatableException {
        if (email == null || orderId == null) {
            throw new ParamErrorException();
        }
        Customer customer = customerDao.find(email);
        if (customer == null) {
            throw new UserNotExistsException();
        }
        Order order = customer.getOrderByOrderId(orderId);
        if (order == null) {
            throw new NoSuchAttributeException();
        }

        //状态判断
        if (!order.isOngoing()) {
            throw new UnupdatableException(order.getState());
        }

        //订单处理, 计算后, 将钱款打到商家用户
        order.setState(OrderState.DONE);
        Restaurant restaurant = order.getRestaurant();
        double finalMoneyToRestaurant = order.getFinalMoneyToRestaurant();
        restaurant.increaceBalance(finalMoneyToRestaurant);

        //数据库处理
        //1.订单
        orderDao.save(order);

        //2.商家
        try {
            restaurantDao.save(restaurant);
        } catch (RuntimeException e) {
            order.setState(OrderState.ONGOING);
            orderDao.save(order);
            throw e;
        }

        //3.订单记录
        try {
            orderLogHelper.createOrderLog(order, OrderState.DONE);
        } catch (RuntimeException e) {
            try {
                restaurant.reduceBalance(finalMoneyToRestaurant);
                order.setState(OrderState.ONGOING);
                restaurantDao.save(restaurant);
                orderDao.save(order);
            } catch (LackOfBalanceException ignored) {
            }
        }
    }

    /**
     * 取消订单
     *
     * @param email
     * @param orderId
     */
    @Override
    public void customerCancelOrder(String email, Integer orderId) throws ParamErrorException, UserNotExistsException, NoSuchAttributeException, UnupdatableException {
        if (email == null || orderId == null) {
            throw new ParamErrorException();
        }
        Customer customer = customerDao.find(email);
        if (customer == null) {
            throw new UserNotExistsException();
        }
        Order order = customer.getOrderByOrderId(orderId);
        if (order == null) {
            throw new NoSuchAttributeException();
        }

        //状态判断
        if (!(order.isOngoing() || order.isUnpaid())) {
            throw new UnupdatableException(order.getState());
        }
        OrderState originalState = order.getState();
        Double refund = 0.0;
        //设置订单
        order.setState(OrderState.CANCELLED);
        //已经进入配餐阶段的order
        if (originalState == OrderState.ONGOING) {
            order.setOrderRefundStrategy(getCurrentOrderRefundStrategy());
            refund = order.getRefundAmount();
            customer.increaceBalance(refund);
        }

        //数据库处理
        //1.订单
        orderDao.save(order);
        //2.customer, 如果失败则卷回
        try {
            customerDao.save(customer);
        } catch (RuntimeException e) {
            order.setState(originalState);
            orderDao.save(order);
        }

        //3.记录
        try {
            orderLogHelper.createOrderLog(order, OrderState.CANCELLED);
        } catch (RuntimeException e) {
            order.setState(originalState);
            orderDao.save(order);
            try {
                customer.reduceBalance(refund);
                customerDao.save(customer);
            } catch (LackOfBalanceException ignored) {
            }
        }
    }
}
