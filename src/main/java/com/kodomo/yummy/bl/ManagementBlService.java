package com.kodomo.yummy.bl;

import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.entity.restaurant.RestaurantModificationInfo;
import com.kodomo.yummy.exceptions.*;

import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-18 11:07
 */
public interface ManagementBlService {

    /**
     * 管理员登录
     *
     * @param id       id
     * @param password password
     * @return 返回Manager实体, 如果错误则返回null
     */
    Manager login(String id, String password);

    /**
     * 管理员注册
     *
     * @param id       id
     * @param password password
     * @return 返回Manager实体, 如果错误返回null
     */
    Manager register(String id, String password);

    /**
     * 审批餐厅
     *
     * @param restaurantId id
     * @param pass         通过为true, 不通过为false
     * @return 结果
     */
    void approveRestaurant(Integer restaurantId, boolean pass) throws UserNotExistsException, UnupdatableException;

    /**
     * 获得manager实体对象
     *
     * @param id
     * @return
     */
    Manager getManagerById(String id);

    /**
     * 获取待审核的修改信息
     *
     * @return
     */
    List<RestaurantModificationInfo> getWaitingRestaurantModificationInfo();

    /**
     * 处理审核的修改信息
     *
     * @param modificationId
     * @param pass
     */
    void confirmModification(Integer modificationId, Boolean pass) throws ParamErrorException, DuplicatedUniqueKeyException, NoSuchAttributeException;

}


