package com.kodomo.yummy.bl.order;

import com.kodomo.yummy.controller.vo.OrderRefundStrategyVo;
import com.kodomo.yummy.controller.vo.OrderSettlementStrategyVo;
import com.kodomo.yummy.dao.ManagerDao;
import com.kodomo.yummy.dao.OrderRefundStrategyDao;
import com.kodomo.yummy.dao.OrderSettlementStrategyDao;
import com.kodomo.yummy.entity.*;
import com.kodomo.yummy.entity.entity_enum.OrderSettlementStrategyType;
import com.kodomo.yummy.exceptions.ParamErrorException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-25 13:45
 */
@Component
public class OrderStrategyHelper {

    private final OrderSettlementStrategyDao orderSettlementStrategyDao;
    private final OrderRefundStrategyDao orderRefundStrategyDao;
    private final ManagerDao managerDao;

    @Autowired
    public OrderStrategyHelper(ManagerDao managerDao, OrderRefundStrategyDao orderRefundStrategyDao, OrderSettlementStrategyDao orderSettlementStrategyDao) {
        this.managerDao = managerDao;
        this.orderRefundStrategyDao = orderRefundStrategyDao;
        this.orderSettlementStrategyDao = orderSettlementStrategyDao;
    }

    OrderSettlementStrategy getCurrentOrderSettlementStrategy() {
        return orderSettlementStrategyDao.getCurrentOrderSettlementStrategy();
    }

    /**
     * 获取当前订单退款策略
     *
     * @return
     */
    OrderRefundStrategy getCurrentOrderRefundStrategy() {
        return orderRefundStrategyDao.getCurrentStrategy();
    }

    void saveNewOrderSettlementStrategy(List<OrderSettlementStrategyVo> vos, String managerId) throws ParamErrorException, UserNotExistsException {
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
     * 保存新的订单退款策略
     *
     * @param vos
     * @param managerId
     * @throws UserNotExistsException 管理员不存在
     */
    void saveNewOrderRefundStrategy(List<OrderRefundStrategyVo> vos, String managerId) throws UserNotExistsException, ParamErrorException {
        Manager manager = managerDao.findById(managerId).orElse(null);
        if (manager == null) {
            throw new UserNotExistsException();
        }

        //生成新的策略
        OrderRefundStrategy orderRefundStrategy = new OrderRefundStrategy();
        Set<OrderRefundStrategyDetail> details = new HashSet<>();
        for (int i = 0; i < vos.size(); i++) {
            OrderRefundStrategyVo thisVo = vos.get(i);
            if (thisVo.getMore() == null || thisVo.getLess() == null || thisVo.getRate() == null) {
                throw new ParamErrorException("时间", "比率");
            }
            for (int j = i + 1; j < vos.size(); j++) {
                if (thisVo.isTimeOverlapping(vos.get(j))) {
                    throw new ParamErrorException("时间");
                }
            }
            OrderRefundStrategyDetail detail = new OrderRefundStrategyDetail();
            detail.setMoreThanOrEquals(thisVo.getMore());
            detail.setLessThan(thisVo.getLess());
            detail.setRefundRate(thisVo.getRate());
            details.add(detail);
        }
        orderRefundStrategy.setDetails(details);
        orderRefundStrategy.setManager(manager);

        //新旧策略处理
        OrderRefundStrategy oldStrategy = orderRefundStrategyDao.getCurrentStrategy();
        if (oldStrategy != null) {
            oldStrategy.setEndTime(new Date());
            orderRefundStrategyDao.save(oldStrategy);
        }
        orderRefundStrategyDao.save(orderRefundStrategy);
    }

}
