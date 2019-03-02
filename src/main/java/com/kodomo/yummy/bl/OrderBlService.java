package com.kodomo.yummy.bl;

import com.kodomo.yummy.controller.vo.OrderRefundStrategyVo;
import com.kodomo.yummy.controller.vo.OrderSettlementStrategyVo;
import com.kodomo.yummy.controller.vo.OrderStatisticsInfoVo;
import com.kodomo.yummy.controller.vo.OrderVo;
import com.kodomo.yummy.entity.order.Order;
import com.kodomo.yummy.entity.order.OrderRefundStrategy;
import com.kodomo.yummy.entity.order.OrderSettlementStrategy;
import com.kodomo.yummy.exceptions.*;

import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-19 11:45
 */
public interface OrderBlService {

    /**
     * 获取当前的订单结算策略
     *
     * @return 当前订单结算策略
     */
    OrderSettlementStrategy getCurrentOrderSettlementStrategy();

    /**
     * 获取当前订单退款策略
     *
     * @return
     */
    OrderRefundStrategy getCurrentOrderRefundStrategy();

    /**
     * 保存新的订单结算策略
     */
    void saveNewOrderSettlementStrategy(List<OrderSettlementStrategyVo> vos, String managerId) throws ParamErrorException, UserNotExistsException;

    /**
     * 保存新的订单退款策略
     *
     * @param vos
     * @param managerId
     */
    void saveNewOrderRefundStrategy(List<OrderRefundStrategyVo> vos, String managerId) throws UserNotExistsException, ParamErrorException;

    /**
     * 根据id查找Order
     *
     * @param id
     * @return
     */
    Order getOrderById(Integer id);

    /**
     * 创建新的订单
     *
     * @param email email
     * @param vo    vo
     * @return
     */
    Order createNewOrder(String email, OrderVo vo) throws ParamErrorException, UserNotExistsException, UnupdatableException, RestaurantHasClosedException;

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
    void payOrder(String email, String password, Integer orderId) throws ParamErrorException, UserNotExistsException, NoSuchAttributeException, OrderTimeOutException, LackOfBalanceException, UnupdatableException, PasswordErrorException;

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
    void customerConfirmOrder(String email, Integer orderId) throws ParamErrorException, UserNotExistsException, NoSuchAttributeException, UnupdatableException;

    void restaurantConfirmOrder(Integer restaurantId, Integer orderId) throws ParamErrorException, UserNotExistsException, NoSuchAttributeException, UnupdatableException;

    /**
     * 取消订单
     *
     * @param email
     * @param orderId
     */
    void customerCancelOrder(String email, Integer orderId) throws ParamErrorException, UserNotExistsException, NoSuchAttributeException, UnupdatableException;

    /**
     * 获取订单统计信息
     *
     * @return
     */
    List<OrderStatisticsInfoVo> getOrderStatisticsVo();
}
