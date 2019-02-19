package com.kodomo.yummy.bl;

import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.exceptions.UnupdatableException;
import com.kodomo.yummy.exceptions.UserNotExistsException;

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
    boolean approveRestaurant(String restaurantId, boolean pass) throws UserNotExistsException, UnupdatableException;


}


