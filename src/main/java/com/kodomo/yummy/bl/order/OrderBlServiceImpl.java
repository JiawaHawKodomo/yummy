package com.kodomo.yummy.bl.order;

import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.bl.restaurant.OfferingHelper;
import com.kodomo.yummy.controller.vo.OrderRefundStrategyVo;
import com.kodomo.yummy.controller.vo.OrderSettlementStrategyVo;
import com.kodomo.yummy.controller.vo.OrderStatisticsInfoVo;
import com.kodomo.yummy.controller.vo.OrderVo;
import com.kodomo.yummy.dao.*;
import com.kodomo.yummy.entity.customer.Customer;
import com.kodomo.yummy.entity.customer.CustomerLevelStrategy;
import com.kodomo.yummy.entity.entity_enum.OrderState;
import com.kodomo.yummy.entity.order.Order;
import com.kodomo.yummy.entity.order.OrderLog;
import com.kodomo.yummy.entity.order.OrderRefundStrategy;
import com.kodomo.yummy.entity.order.OrderSettlementStrategy;
import com.kodomo.yummy.entity.restaurant.Restaurant;
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

    private final OrderHelper orderHelper;
    private final OrderDao orderDao;
    private final OrderCreator orderCreator;
    private final OrderLogDao orderLogDao;
    private final CustomerDao customerDao;
    private final OrderLogHelper orderLogHelper;
    private final RestaurantDao restaurantDao;
    private final OrderStrategyHelper orderStrategyHelper;
    private final OfferingHelper offeringHelper;

    @Autowired
    public OrderBlServiceImpl(OrderDao orderDao, OrderCreator orderCreator, OrderLogDao orderLogDao, CustomerDao customerDao, OrderLogHelper orderLogHelper, RestaurantDao restaurantDao, OrderStrategyHelper orderStrategyHelper, OrderHelper orderHelper, OfferingHelper offeringHelper) {
        this.orderDao = orderDao;
        this.orderCreator = orderCreator;
        this.orderLogDao = orderLogDao;
        this.customerDao = customerDao;
        this.orderLogHelper = orderLogHelper;
        this.restaurantDao = restaurantDao;
        this.orderStrategyHelper = orderStrategyHelper;
        this.orderHelper = orderHelper;
        this.offeringHelper = offeringHelper;
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
    public Order createNewOrder(String email, OrderVo vo, Date date) throws ParamErrorException, UserNotExistsException, UnupdatableException, RestaurantHasClosedException, ExceedRemainException {
        Order order = orderCreator.createNewOrder(email, vo);

        //该订单中的商品库存-1
        try {
            offeringHelper.remainingReduce(order);
        } catch (ExceedRemainException e) {
            //消减库存出错, 回滚, 删除订单
            orderDao.deleteById(order.getOrderId());
        }

        //生成新的订单记录
        OrderLog orderLog = new OrderLog();
        orderLog.setOrder(order);
        orderLog.setDate(date);
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
    public void payOrder(String email, String password, Integer orderId, Date date) throws ParamErrorException, UserNotExistsException, NoSuchAttributeException, OrderTimeOutException, LackOfBalanceException, UnupdatableException, PasswordErrorException {
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
        if (createTime == null || date.getTime() - createTime.getTime() >= maxPayTime * 60 * 1000) {
            //超时
            throw new OrderTimeOutException(createTime);
        }

        //订单支付
        Double amount = order.getTotalPriceAfterDiscount();
        customer.reduceBalance(amount);
        order.setState(OrderState.ONGOING);

        //数据库处理
        customerDao.save(customer);
        orderDao.save(order);
        orderLogHelper.createOrderLog(order, OrderState.ONGOING, date);
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
    public void customerConfirmOrder(String email, Integer orderId, Date date) throws ParamErrorException, UserNotExistsException, NoSuchAttributeException, UnupdatableException {
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
        orderDao.save(order);
        restaurantDao.save(restaurant);
        orderLogHelper.createOrderLog(order, OrderState.DONE, date);
        //增加经验
        setLevelForCustomer(customer.getEmail(), order.getCustomerLevelStrategy());
    }

    @Override
    public void restaurantConfirmOrder(Integer restaurantId, Integer orderId) throws ParamErrorException, UserNotExistsException, NoSuchAttributeException, UnupdatableException {
        if (restaurantId == null || orderId == null) {
            throw new ParamErrorException();
        }
        Restaurant restaurant = restaurantDao.find(restaurantId);
        if (restaurant == null) {
            throw new UserNotExistsException();
        }
        Order order = restaurant.getOrderById(orderId);
        if (order == null) {
            throw new NoSuchAttributeException();
        }

        //状态判断
        if (!order.isOngoing()) {
            throw new UnupdatableException(order.getState());
        }
        //订单处理, 计算后, 将钱款打到商家用户
        order.setState(OrderState.DONE);
        double finalMoneyToRestaurant = order.getFinalMoneyToRestaurant();
        restaurant.increaceBalance(finalMoneyToRestaurant);
        //数据库处理
        orderDao.save(order);
        restaurantDao.save(restaurant);
        orderLogHelper.createOrderLog(order, OrderState.DONE, new Date());
        //增加经验
        setLevelForCustomer(order.getCustomerEmail(), order.getCustomerLevelStrategy());
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
        Double refund;
        //设置订单
        order.setState(OrderState.CANCELLED);
        //已经进入配餐阶段的order
        if (originalState == OrderState.ONGOING) {
            order.setOrderRefundStrategy(getCurrentOrderRefundStrategy());
            refund = order.getRefundAmount();
            customer.increaceBalance(refund);
        }

        //库存回退
        offeringHelper.remainingIncreaseWhenCancelOrder(order);

        //数据库处理
        orderDao.save(order);
        customerDao.save(customer);
        orderLogHelper.createOrderLog(order, OrderState.CANCELLED, new Date());
    }

    private void setLevelForCustomer(String email, CustomerLevelStrategy strategy) {
        Customer customer = customerDao.find(email);
        if (customer != null) {
            customer.setLevel(strategy.calculateLevel(customer.getTotalConsumptionAmount()));
            customerDao.save(customer);
        }
    }

    /**
     * 获取订单统计信息
     *
     * @return
     */
    @Override
    public List<OrderStatisticsInfoVo> getOrderStatisticsVo() {
        return orderHelper.mapToStatisticsVo(orderDao.findAll());
    }
}
