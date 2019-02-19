package com.kodomo.yummy.bl;

import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.entity.OrderSettlementStrategy;
import com.kodomo.yummy.exceptions.ParamErrorException;

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
     * @param jsonMap 前端json
     */
    void saveNewOrderSettlementStrategy(List<Map<String, String>> jsonMap, Manager manager) throws ParamErrorException;
}
