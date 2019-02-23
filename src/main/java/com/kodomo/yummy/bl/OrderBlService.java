package com.kodomo.yummy.bl;

import com.kodomo.yummy.controller.vo.OrderSettlementStrategyVo;
import com.kodomo.yummy.controller.vo.OrderVo;
import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.entity.Order;
import com.kodomo.yummy.entity.OrderSettlementStrategy;
import com.kodomo.yummy.exceptions.DatabaseUnknownException;
import com.kodomo.yummy.exceptions.ParamErrorException;
import com.kodomo.yummy.exceptions.UnupdatableException;
import com.kodomo.yummy.exceptions.UserNotExistsException;

import java.util.List;
import java.util.Map;

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
     * 保存新的订单结算策略
     *
     */
    void saveNewOrderSettlementStrategy(List<OrderSettlementStrategyVo> vos, String managerId) throws ParamErrorException, UserNotExistsException;

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
    Order createNewOrder(String email, OrderVo vo) throws ParamErrorException, UserNotExistsException, DatabaseUnknownException, UnupdatableException;
}
