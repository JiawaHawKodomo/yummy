package com.kodomo.yummy.bl.customer;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.dao.CustomerDao;
import com.kodomo.yummy.entity.Customer;
import com.kodomo.yummy.exceptions.ParamErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-16 14:45
 */
@Service
public class CustomerBlServiceImpl implements CustomerBlService {

    private final CustomerDao customerDao;
    private final CustomerCreator customerCreator;

    @Autowired
    public CustomerBlServiceImpl(CustomerDao customerDao, CustomerCreator customerCreator) {
        this.customerDao = customerDao;
        this.customerCreator = customerCreator;
    }

    /**
     * 登录或注册登录, 如果数据库中没有该email信息则自动注册
     *
     * @param email
     * @param password
     * @return customer信息, 如果匹配错误返回null
     */
    @Override
    public Customer loginOrRegister(String email, String password) throws ParamErrorException {
        Customer customer = customerDao.findById(email).orElse(null);
        if (customer != null) {
            //寻找到customer
            if (password.equals(customer.getPassword())) {
                //验证成功
                return customer;
            } else {
                //验证失败
                return null;
            }
        } else {
            //未找到账号信息, 自动注册
            customer = customerCreator.createNewCustomerForDatabase(email, password);
            return customer;
        }
    }

    /**
     * 用户激活
     *
     * @param code
     * @return
     */
    @Override
    public boolean activate(String code) {
        try {
            Customer customer = customerCreator.activate(code);
            return customer != null;
        } catch (ParamErrorException e) {
            return false;
        }
    }

    /**
     * 根据email查询customer
     *
     * @param email email
     * @return entity
     */
    @Override
    public Customer getCustomerEntityByEmail(String email) {
        return customerDao.findById(email).orElse(null);
    }
}
