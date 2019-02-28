package com.kodomo.yummy.bl;

import com.kodomo.yummy.controller.vo.*;
import com.kodomo.yummy.entity.Order;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.exceptions.*;

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
    Restaurant registerRestaurant(String name, String password, String tel, String time, String type, String note, String city, Double lat, Double lng, String block, String point, String addressNote) throws ParamErrorException, DuplicatedUniqueKeyException;

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

    /**
     * id查询餐厅
     *
     * @param id
     * @return
     */
    Restaurant getRestaurantById(Integer id);

    /**
     * 修改餐厅的餐品类型
     *
     * @param rid
     * @param newTypes
     */
    void updateRestaurantOfferingType(Integer rid, List<OfferingTypeVo> newTypes) throws ParamErrorException, UserNotExistsException, DuplicatedUniqueKeyException, UnupdatableException;

    /**
     * 保存餐品信息
     *
     * @param rid
     * @param vo
     */
    void saveOffering(Integer rid, OfferingVo vo) throws ParamErrorException, UserNotExistsException, UnupdatableException;

    /**
     * 删除商品信息
     *
     * @param rid
     * @param offeringId
     * @throws ParamErrorException      参数为null
     * @throws NoSuchAttributeException 不存在该商品
     * @throws UnupdatableException     餐厅不正确
     */
    void deleteOffering(Integer rid, Integer offeringId) throws ParamErrorException, NoSuchAttributeException, UnupdatableException;

    /**
     * 添加满减策略
     *
     * @param rid rid
     * @param vos vos
     */
    void addRestaurantStrategy(Integer rid, List<RestaurantStrategyVo> vos) throws ParamErrorException, UserNotExistsException;

    /**
     * 删除满减策略
     *
     * @param rid
     * @param strategyId
     * @throws ParamErrorException      参数为null
     * @throws NoSuchAttributeException 没有该策略
     * @throws UnupdatableException     不是自己的策略, 无法修改
     */
    void deleteRestaurantStrategy(Integer rid, Integer strategyId) throws NoSuchAttributeException, ParamErrorException, UnupdatableException;

    /**
     * 计算获得统计信息
     *
     * @param rid rid
     * @return
     */
    List<OrderStatisticsInfoVo> getStatisticsInfos(Integer rid);

    List<Order> getOrdersByTimeOfCustomer(Integer rid, String time, String format);

    /**
     * 获取统计信息
     *
     * @return
     */
    List<RestaurantStatisticsVo> getRestaurantStatisticsInfo();

    /**
     * 提交修改餐厅的信息的申请
     *
     * @param vo
     * @param restaurantId
     */
    void submitModification(RestaurantModificationVo vo, Integer restaurantId) throws ParamErrorException, DuplicatedUniqueKeyException, UserNotExistsException, DuplicatedSubmitException;
}
