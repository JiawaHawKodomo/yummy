package com.kodomo.yummy.config;

import com.kodomo.yummy.bl.ManagementBlService;
import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.dao.ManagerDao;
import com.kodomo.yummy.dao.OrderSettlementStrategyDao;
import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.entity.OrderSettlementStrategy;
import com.kodomo.yummy.exceptions.ParamErrorException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import com.kodomo.yummy.util.Utility;
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

    @Autowired
    public ManagerConfig(ManagementBlService managementBlService, OrderBlService orderBlService) {
        this.managementBlService = managementBlService;
        this.orderBlService = orderBlService;
    }

    /**
     * 启动项目后插入超级管理员
     */
    @PostConstruct
    public void createManager() {
        Manager manager = managementBlService.register(superManagerId, superManagerPassword);
        //检查默认的策略
        checkDefaultOrderStrategy(manager.getManagerId());
    }

    /**
     * 插入默认策略
     */
    private void checkDefaultOrderStrategy(String managerId) {
        OrderSettlementStrategy currentStrategy = orderBlService.getCurrentOrderSettlementStrategy();
        if (currentStrategy == null) {
            //插入默认策略
            try {
                orderBlService.saveNewOrderSettlementStrategy(new ArrayList<>(), managerId);
            } catch (ParamErrorException | UserNotExistsException ignored) {
            }
        }
    }
}
