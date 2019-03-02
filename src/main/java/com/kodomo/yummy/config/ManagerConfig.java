package com.kodomo.yummy.config;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.bl.ManagementBlService;
import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.controller.vo.CustomerLevelStrategyVo;
import com.kodomo.yummy.entity.customer.CustomerLevelStrategy;
import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.entity.order.OrderRefundStrategy;
import com.kodomo.yummy.entity.order.OrderSettlementStrategy;
import com.kodomo.yummy.exceptions.ParamErrorException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

/**
 * 自动插入超级管理员
 *
 * @author Shuaiyu Yao
 * @create 2019-02-18 10:46
 */
@Component
public class ManagerConfig {

    @Value("${yummy-system.super-manager.id}")
    private String superManagerId;
    @Value("${yummy-system.super-manager.password}")
    private String superManagerPassword;

    private final ManagementBlService managementBlService;
    private final OrderBlService orderBlService;
    private final CustomerBlService customerBlService;

    @Autowired
    public ManagerConfig(ManagementBlService managementBlService, OrderBlService orderBlService, CustomerBlService customerBlService) {
        this.managementBlService = managementBlService;
        this.orderBlService = orderBlService;
        this.customerBlService = customerBlService;
    }

    /**
     * 启动项目后插入超级管理员
     */
    @PostConstruct
    public void createManager() {
        Manager manager = managementBlService.register(superManagerId, superManagerPassword);
        //检查默认的策略
        checkDefaultOrderSettlementStrategy(manager.getManagerId());
        //检查默认订单退款策略
        checkDefaultOrderRefundStrategy(manager.getManagerId());
        //检查会员策略
        checkDefaultCustomerLevelStrategy(manager.getManagerId());
    }

    /**
     * 插入默认订单结算策略
     */
    private void checkDefaultOrderSettlementStrategy(String managerId) {
        OrderSettlementStrategy currentStrategy = orderBlService.getCurrentOrderSettlementStrategy();
        if (currentStrategy == null) {
            //插入默认策略
            try {
                orderBlService.saveNewOrderSettlementStrategy(new ArrayList<>(), managerId);
            } catch (ParamErrorException | UserNotExistsException ignored) {
                throw new RuntimeException(ignored);
            }
        }
    }

    /**
     * 插入默认订单退款策略
     */
    private void checkDefaultOrderRefundStrategy(String managerId) {
        OrderRefundStrategy currentStrategy = orderBlService.getCurrentOrderRefundStrategy();
        if (currentStrategy == null) {
            //插入默认策略
            try {
                orderBlService.saveNewOrderRefundStrategy(new ArrayList<>(), managerId);
            } catch (UserNotExistsException | ParamErrorException ignored) {
                throw new RuntimeException(ignored);
            }
        }
    }

    /**
     * 插入默认会员等级策略
     *
     * @param managerId
     */
    private void checkDefaultCustomerLevelStrategy(String managerId) {
        CustomerLevelStrategy customerLevelStrategy = customerBlService.getCurrentCustomerLevelStrategy();
        if (customerLevelStrategy == null) {
            //插入默认策略
            try {
                customerBlService.saveCustomerLevelStrategy(new ArrayList<CustomerLevelStrategyVo>() {{
                    CustomerLevelStrategyVo vo = new CustomerLevelStrategyVo();
                    vo.setLevel(1);
                    vo.setAmount(0.0);
                    vo.setRate(0.0);
                    add(vo);
                }}, managerId);
            } catch (UserNotExistsException | ParamErrorException ignored) {
                throw new RuntimeException(ignored);
            }
        }
    }
}
