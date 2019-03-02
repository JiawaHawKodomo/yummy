package com.kodomo.yummy.bl.management;

import com.kodomo.yummy.bl.ManagementBlService;
import com.kodomo.yummy.bl.restaurant.RestaurantEntityHelper;
import com.kodomo.yummy.bl.restaurant.RestaurantMessageHelper;
import com.kodomo.yummy.dao.ManagerDao;
import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.entity.restaurant.RestaurantModificationInfo;
import com.kodomo.yummy.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author Shuaiyu Yao
 * @create 2019-02-18 11:08
 */
@Service
public class ManagementBlServiceImpl implements ManagementBlService {

    @Value("${yummy-system.text.restaurant.register.approve}")
    private String registerApprovedText;
    @Value("${yummy-system.text.restaurant.register.not-approve}")
    private String registerNotApprovedText;

    private final ManagerDao managerDao;
    private final RestaurantDao restaurantDao;
    private final RestaurantMessageHelper restaurantMessageHelper;
    private final RestaurantEntityHelper restaurantEntityHelper;

    @Autowired
    public ManagementBlServiceImpl(ManagerDao managerDao, RestaurantDao restaurantDao, RestaurantMessageHelper restaurantMessageHelper, RestaurantEntityHelper restaurantEntityHelper) {
        this.managerDao = managerDao;
        this.restaurantDao = restaurantDao;
        this.restaurantMessageHelper = restaurantMessageHelper;
        this.restaurantEntityHelper = restaurantEntityHelper;
    }

    @Override
    public Manager login(String id, String password) {
        if (id == null || password == null || id.equals("") || password.equals(""))
            return null;

        Manager manager = managerDao.findById(id).orElse(null);
        if (manager == null) return null;
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
    public void approveRestaurant(Integer restaurantId, boolean pass) throws UserNotExistsException, UnupdatableException {
        //检查id
        if (restaurantId == null)
            throw new UserNotExistsException(restaurantId + "不存在");

        Restaurant restaurant;
        restaurant = restaurantDao.findById(restaurantId).orElse(null);
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
        restaurantDao.save(restaurant);

        //发送消息
        try {
            String messageText = pass ? registerApprovedText : registerNotApprovedText;
            restaurantMessageHelper.sendMessage(restaurantId, messageText);
        } catch (ParamErrorException ignored) {
        }
    }

    /**
     * 获得manager实体对象
     *
     * @param id
     * @return
     */
    @Override
    public Manager getManagerById(String id) {
        return managerDao.find(id);
    }

    /**
     * 获取待审核的修改信息
     *
     * @return
     */
    @Override
    public List<RestaurantModificationInfo> getWaitingRestaurantModificationInfo() {
        return restaurantEntityHelper.getWaitingRestaurantModificationInfo();
    }

    /**
     * 处理审核的修改信息
     *
     * @param modificationId
     * @param pass
     */
    @Override
    public void confirmModification(Integer modificationId, Boolean pass) throws ParamErrorException, DuplicatedUniqueKeyException, NoSuchAttributeException {
        restaurantEntityHelper.confirmModification(modificationId, pass);
    }
}
