package com.kodomo.yummy.bl;

import com.kodomo.yummy.entity.Customer;
import com.kodomo.yummy.exceptions.ParamErrorException;

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
}
