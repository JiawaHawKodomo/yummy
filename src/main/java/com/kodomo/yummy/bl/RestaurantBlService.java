package com.kodomo.yummy.bl;

import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.entity.UserState;
import com.kodomo.yummy.exceptions.ParamErrorException;

import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-17 17:01
 */
public interface RestaurantBlService {

    /**
     * 注册餐厅信息
     *
     * @param name     name
     * @param password password
     * @param tel      tel
     * @param time     time
     * @param type     type
     * @param note     note
     * @param city     city
     * @param lat      lat
     * @param lng      lng
     * @param block    block
     * @param point    point
     * @return 餐厅实体对象
     * @throws ParamErrorException 参数错误,创建失败
     */
    Restaurant registerRestaurant(String name, String password, String tel, String time, String type, String note, String city, Double lat, Double lng, String block, String point, String addressNote) throws ParamErrorException;

    /**
     * 登录
     *
     * @param id       id
     * @param password password
     * @return 如果验证成功, 则返回餐厅实体对象, 否则返回null
     */
    Restaurant login(String id, String password);

    /**
     * 按状态获取餐厅信息
     *
     * @param state state
     * @return list
     */
    List<Restaurant> getRestaurantByState(UserState state);
}
