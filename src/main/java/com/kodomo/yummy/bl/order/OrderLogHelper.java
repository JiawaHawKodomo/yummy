package com.kodomo.yummy.bl.order;

import com.kodomo.yummy.dao.OrderLogDao;
import com.kodomo.yummy.entity.Order;
import com.kodomo.yummy.entity.OrderLog;
import com.kodomo.yummy.entity.entity_enum.OrderState;
import com.kodomo.yummy.exceptions.ParamErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-25 8:22
 */
@Component
public class OrderLogHelper {

    private final OrderLogDao orderLogDao;

    @Autowired
    public OrderLogHelper(OrderLogDao orderLogDao) {
        this.orderLogDao = orderLogDao;
    }

    /**
     * 创建orderLog并存储
     *
     * @param order
     * @param state
     * @return
     * @throws ParamErrorException 参数错误
     */
    OrderLog createOrderLog(Order order, OrderState state) throws ParamErrorException {
        if (order == null || state == null) {
            throw new ParamErrorException("订单编号, 状态");
        }

        OrderLog orderLog = new OrderLog();
        orderLog.setOrder(order);
        orderLog.setToState(state);
        return orderLogDao.save(orderLog);
    }
}
