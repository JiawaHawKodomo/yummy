package com.kodomo.yummy.bl;

import com.kodomo.yummy.entity.Customer;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.exceptions.*;

import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-16 14:42
 */
public interface CustomerBlService {

    /**
     * 登录或注册登录, 如果数据库中没有该email信息则自动注册
     *
     * @param email
     * @param password
     * @return customer信息
     */
    Customer loginOrRegister(String email, String password) throws ParamErrorException;

    /**
     * 用户激活
     *
     * @param code
     * @return
     */
    boolean activate(String code);

    /**
     * 根据email查询customer
     *
     * @param email email
     * @return entity
     */
    Customer getCustomerEntityByEmail(String email);

    /**
     * 修改客户信息
     *
     * @param email     email
     * @param name      name
     * @param telephone telephone
     * @throws ParamErrorException    输入参数不正确
     * @throws UserNotExistsException 用户不存在
     * @throws UnupdatableException   用户状态不正确
     */
    void updateCustomerInfo(String email, String name, String telephone) throws ParamErrorException, UserNotExistsException, UnupdatableException;

    /**
     * 修改客户密码
     *
     * @param email    email
     * @param password password
     * @throws ParamErrorException    输入参数不正确
     * @throws UserNotExistsException 用户不存在
     * @throws UnupdatableException   用户状态不正确
     * @throws PasswordErrorException 旧密码不正确
     */
    void updateCustomerPassword(String email, String old, String password) throws ParamErrorException, UserNotExistsException, UnupdatableException, PasswordErrorException;

    /**
     * 创建用户地址信息
     *
     * @param email     email
     * @param block     block
     * @param point     point
     * @param note      note
     * @param city      city
     * @param telephone telephone
     * @param lat       lat
     * @param lng       lng
     */
    void addLocationForCustomer(String email, String block, String point, String note, String city, String telephone, Double lat, Double lng) throws ParamErrorException, UserNotExistsException, UnupdatableException;

    /**
     * 删除用户地址信息, 将地址改为无法使用, 在数据库中保留
     *
     * @param email      email
     * @param locationId locationId
     * @throws ParamErrorException      id错误
     * @throws UserNotExistsException   用户不存在
     * @throws UnupdatableException     用户状态不正确
     * @throws NoSuchAttributeException 没有该location
     */
    void deleteLocationForCustomer(String email, Integer locationId) throws ParamErrorException, UserNotExistsException, UnupdatableException, NoSuchAttributeException;

    /**
     * 注销账号
     *
     * @param email    email
     * @param password password
     */
    void closeCustomer(String email, String password) throws ParamErrorException, UserNotExistsException, UnupdatableException, PasswordErrorException;

    /**
     * 查找范围内的餐厅
     *
     * @param email      email
     * @param locationId locationId
     * @return
     * @throws ParamErrorException      传入参数为null
     * @throws NoSuchAttributeException 没有该地址
     * @throws UserNotExistsException   没有该customer
     */
    List<Restaurant> getRestaurantWithinDistributionDistance(String email, Integer locationId) throws ParamErrorException, NoSuchAttributeException, UserNotExistsException;

    /**
     * @param email      email
     * @param locationId locationId
     * @param keyWord    搜索关键字
     * @return
     * @throws ParamErrorException      传入参数为null
     * @throws NoSuchAttributeException 没有该地址
     * @throws UserNotExistsException   没有该customer
     */
    List<Restaurant> getSearchedRestaurant(String email, Integer locationId, String keyWord) throws ParamErrorException, UserNotExistsException, NoSuchAttributeException;

    /**
     * 充值
     *
     * @param customerEmail email
     * @param amount        数值
     */
    void recharge(String customerEmail, Double amount) throws ParamErrorException, UserNotExistsException, UnupdatableException, DatabaseUnknownException;
}

