package com.kodomo.yummy.config;

import com.kodomo.yummy.dao.ManagerDao;
import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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

    private final ManagerDao managerDao;

    @Autowired
    public ManagerConfig(ManagerDao managerDao) {
        this.managerDao = managerDao;
    }

    /**
     * 启动项目后插入超级管理员
     */
    @PostConstruct
    public void createManager() {
        Manager manager = new Manager();
        manager.setManagerId(superManagerId);
        manager.setPassword(superManagerPassword);

        managerDao.save(manager);
    }
}
