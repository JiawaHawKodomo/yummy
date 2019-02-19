package com.kodomo.yummy.bl.order;

import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.dao.OrderSettlementStrategyDao;
import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.entity.OrderSettlementStrategy;
import com.kodomo.yummy.entity.OrderSettlementStrategyDetail;
import com.kodomo.yummy.entity.entity_enum.OrderSettlementStrategyType;
import com.kodomo.yummy.exceptions.ParamErrorException;
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

    @Autowired
    public OrderBlServiceImpl(OrderSettlementStrategyDao orderSettlementStrategyDao) {
        this.orderSettlementStrategyDao = orderSettlementStrategyDao;
    }

    @Override
    public OrderSettlementStrategy getCurrentOrderSettlementStrategy() {
        return orderSettlementStrategyDao.getCurrentOrderSettlementStrategy();
    }

    @Override
    public void saveNewOrderSettlementStrategy(List<Map<String, String>> jsonMap, Manager manager) throws ParamErrorException {
        //生成新的策略
        OrderSettlementStrategy newStrategy = new OrderSettlementStrategy();
        Set<OrderSettlementStrategyDetail> details = new HashSet<>();
        for (Map<String, String> m : jsonMap) {
            String key = m.get("key");
            Double rate = Double.valueOf(m.get("rate"));
            OrderSettlementStrategyType type = OrderSettlementStrategyType.getByIndex(Integer.parseInt(m.get("type")));

            if (key == null || type == null) {
                throw new ParamErrorException("参数填写错误");
            }

            OrderSettlementStrategyDetail detail = new OrderSettlementStrategyDetail();
            detail.setKey(key);
            detail.setRate(rate);
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
}
