package com.kodomo.yummy.bl.order;

import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.controller.vo.OrderSettlementStrategyVo;
import com.kodomo.yummy.controller.vo.OrderVo;
import com.kodomo.yummy.dao.ManagerDao;
import com.kodomo.yummy.dao.OrderDao;
import com.kodomo.yummy.dao.OrderLogDao;
import com.kodomo.yummy.dao.OrderSettlementStrategyDao;
import com.kodomo.yummy.entity.*;
import com.kodomo.yummy.entity.entity_enum.OrderSettlementStrategyType;
import com.kodomo.yummy.entity.entity_enum.OrderState;
import com.kodomo.yummy.exceptions.DatabaseUnknownException;
import com.kodomo.yummy.exceptions.ParamErrorException;
import com.kodomo.yummy.exceptions.UnupdatableException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @author Shuaiyu Yao
 * @create 2019-02-19 11:45
 */
@Service
public class OrderBlServiceImpl implements OrderBlService {

    private final OrderSettlementStrategyDao orderSettlementStrategyDao;
    private final OrderDao orderDao;
    private final ManagerDao managerDao;
    private final OrderCreator orderCreator;
    private final OrderLogDao orderLogDao;

    @Autowired
    public OrderBlServiceImpl(OrderSettlementStrategyDao orderSettlementStrategyDao, OrderDao orderDao, ManagerDao managerDao, OrderCreator orderCreator, OrderLogDao orderLogDao) {
        this.orderSettlementStrategyDao = orderSettlementStrategyDao;
        this.orderDao = orderDao;
        this.managerDao = managerDao;
        this.orderCreator = orderCreator;
        this.orderLogDao = orderLogDao;
    }

    @Override
    public OrderSettlementStrategy getCurrentOrderSettlementStrategy() {
        return orderSettlementStrategyDao.getCurrentOrderSettlementStrategy();
    }

    @Override
    public void saveNewOrderSettlementStrategy(List<OrderSettlementStrategyVo> vos, String managerId) throws ParamErrorException, UserNotExistsException {
        Manager manager = managerDao.findById(managerId).orElse(null);
        if (manager == null) {
            throw new UserNotExistsException();
        }

        //生成新的策略
        OrderSettlementStrategy newStrategy = new OrderSettlementStrategy();
        Set<OrderSettlementStrategyDetail> details = new HashSet<>();
        for (OrderSettlementStrategyVo vo : vos) {
            OrderSettlementStrategyType type = OrderSettlementStrategyType.getByIndex(vo.getType());

            if (vo.getKey() == null || type == null) {
                throw new ParamErrorException("参数填写错误");
            }

            OrderSettlementStrategyDetail detail = new OrderSettlementStrategyDetail();
            detail.setKey(vo.getKey());
            detail.setRate(vo.getRate());
            detail.setType(type);
            details.add(detail);
        }
        newStrategy.setDetails(details);
        newStrategy.setManager(manager);

        //新旧策略处理
        OrderSettlementStrategy oldStrategy = orderSettlementStrategyDao.getCurrentOrderSettlementStrategy();
        if (oldStrategy != null) {
            oldStrategy.setEndTime(new Date());
            orderSettlementStrategyDao.save(oldStrategy);
        }
        orderSettlementStrategyDao.save(newStrategy);
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
    public Order createNewOrder(String email, OrderVo vo) throws ParamErrorException, UserNotExistsException, DatabaseUnknownException, UnupdatableException {
        Order order = orderCreator.createNewOrder(email, vo);

        //生成新的订单记录
        OrderLog orderLog = new OrderLog();
        orderLog.setOrder(order);
        orderLog.setToState(OrderState.UNPAID);
        try {
            orderLogDao.save(orderLog);
        } catch (Exception e) {
            throw new DatabaseUnknownException(e);
        }
        return order;
    }
}
