package com.kodomo.yummy.bl.management;

import com.kodomo.yummy.bl.ManagementBlService;
import com.kodomo.yummy.dao.ManagerDao;
import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.exceptions.UnupdatableException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-18 11:08
 */
@Service
public class ManagementBlServiceImpl implements ManagementBlService {

    private final ManagerDao managerDao;
    private final RestaurantDao restaurantDao;

    @Autowired
    public ManagementBlServiceImpl(ManagerDao managerDao, RestaurantDao restaurantDao) {
        this.managerDao = managerDao;
        this.restaurantDao = restaurantDao;
    }

    @Override
    public Manager login(String id, String password) {
        if (id == null || password == null || id.equals("") || password.equals(""))
            return null;

        Manager manager = managerDao.findById(id).orElse(null);
        if (password.equals(manager.getPassword())) {
            return manager;
        } else
            return null;
    }

    /**
     * 管理员注册
     *
     * @param id       id
     * @param password password
     * @return 返回Manager实体, 如果错误返回null
     */
    @Override
    public Manager register(String id, String password) {
        if (id == null || password == null || id.equals("") || password.equals(""))
            return null;

        Manager manager = new Manager();
        manager.setManagerId(id);
        manager.setPassword(password);
        try {
            return managerDao.save(manager);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 审批餐厅
     *
     * @param restaurantId id
     * @param pass         通过为true, 不通过为false
     * @return 结果
     */
    @Override
    public boolean approveRestaurant(String restaurantId, boolean pass) throws UserNotExistsException, UnupdatableException {
        //检查id
        if (restaurantId == null || Objects.equals(restaurantId, ""))
            throw new UserNotExistsException(restaurantId + "不存在");

        Integer idInteger;
        try {
            idInteger = Integer.valueOf(restaurantId);
        } catch (Exception e) {
            throw new UserNotExistsException(restaurantId + "不存在");
        }

        Restaurant restaurant;
        try {
            restaurant = restaurantDao.findById(idInteger).orElse(null);
        } catch (Exception e) {
            return false;
        }
        UserState currentState = restaurant.getState();
        //检查状态
        if (currentState != null && currentState != UserState.UNACTIVATED) {
            throw new UnupdatableException(restaurantId + "状态为" + currentState.getText() + ",无法再次审批");
        }

        //设置状态
        if (pass) {
            restaurant.setState(UserState.ACTIVATED);
        } else {
            restaurant.setState(UserState.NOT_APPROVED);
        }

        //保存
        try {
            restaurantDao.save(restaurant);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
