package com.kodomo.yummy.bl.customer;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.bl.location.LocationHelper;
import com.kodomo.yummy.bl.util.ValidatingHelper;
import com.kodomo.yummy.controller.vo.OrderStatisticsInfoVo;
import com.kodomo.yummy.dao.CustomerDao;
import com.kodomo.yummy.dao.CustomerRechargeLogDao;
import com.kodomo.yummy.dao.LocationDao;
import com.kodomo.yummy.entity.*;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-16 14:45
 */
@Service
public class CustomerBlServiceImpl implements CustomerBlService {

    private final CustomerDao customerDao;
    private final CustomerCreator customerCreator;
    private final LocationHelper locationHelper;
    private final ValidatingHelper validatingHelper;
    private final LocationDao locationDao;
    private final CustomerPlaceHelper customerPlaceHelper;
    private final CustomerRechargeLogDao customerRechargeLogDao;
    private final CustomerStatisticsHelper customerStatisticsHelper;

    @Autowired
    public CustomerBlServiceImpl(CustomerDao customerDao, CustomerCreator customerCreator, LocationHelper locationHelper, ValidatingHelper validatingHelper, LocationDao locationDao, CustomerPlaceHelper customerPlaceHelper, CustomerRechargeLogDao customerRechargeLogDao, CustomerStatisticsHelper customerStatisticsHelper) {
        this.customerDao = customerDao;
        this.customerCreator = customerCreator;
        this.locationHelper = locationHelper;
        this.validatingHelper = validatingHelper;
        this.locationDao = locationDao;
        this.customerPlaceHelper = customerPlaceHelper;
        this.customerRechargeLogDao = customerRechargeLogDao;
        this.customerStatisticsHelper = customerStatisticsHelper;
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
            if (!password.equals(customer.getPassword()) || customer.getState() == UserState.CLOSED) {
                //验证成功
                return null;
            } else {
                //验证失败
                return customer;
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

    /**
     * 修改客户信息
     *
     * @param email     email
     * @param name      name
     * @param telephone telephone
     */
    @Override
    public void updateCustomerInfo(String email, String name, String telephone) throws ParamErrorException, UserNotExistsException, UnupdatableException {
        if (email == null || name == null || telephone == null || name.equals("") || telephone.equals("")) {
            //参数不正确
            throw new ParamErrorException();
        }

        Customer customer = customerDao.findById(email).orElse(null);
        if (customer == null) {
            //用户不存在
            throw new UserNotExistsException();
        }

        if (customer.getState() != UserState.ACTIVATED) {
            //用户状态不正确
            throw new UnupdatableException(customer.getState());
        }

        customer.setName(name);
        customer.setTelephone(telephone);
        customerDao.save(customer);
    }

    /**
     * 修改客户密码
     *
     * @param email    email
     * @param password password
     */
    @Override
    public void updateCustomerPassword(String email, String old, String password) throws ParamErrorException, UserNotExistsException, UnupdatableException, PasswordErrorException {
        if (email == null || old == null || old.equals("") || password == null || password.equals("")) {
            //参数不正确
            throw new ParamErrorException();
        }

        Customer customer = customerDao.findById(email).orElse(null);
        if (customer == null) {
            //用户不存在
            throw new UserNotExistsException();
        }

        if (customer.getState() != UserState.ACTIVATED) {
            //用户状态不正确
            throw new UnupdatableException(customer.getState());
        }

        if (!old.equals(customer.getPassword())) {
            //旧密码不正确
            throw new PasswordErrorException();
        }

        customer.setPassword(password);
        customerDao.save(customer);
    }

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
    @Override
    public void addLocationForCustomer(String email, String block, String point, String note, String city, String telephone, Double lat, Double lng) throws ParamErrorException, UserNotExistsException, UnupdatableException {
        if (email == null || !validatingHelper.isTelephone(telephone)) {
            //参数错误
            throw new ParamErrorException();
        }

        Location location = locationHelper.createLocation(block, point, note, city, telephone, lat, lng);
        Customer customer = customerDao.findById(email).orElse(null);
        if (customer == null) {
            //用户不存在
            throw new UserNotExistsException();
        }
        if (customer.getState() != UserState.ACTIVATED) {
            //用户状态不正确
            throw new UnupdatableException(customer.getState());
        }

        //添加地址信息
        customer.addLocation(location);
        customerDao.save(customer);
    }

    /**
     * 删除用户地址信息, 将地址改为无法使用, 在数据库中保留
     *
     * @param email      email
     * @param locationId locationId
     */
    @Override
    public void deleteLocationForCustomer(String email, Integer locationId) throws ParamErrorException, UserNotExistsException, UnupdatableException, NoSuchAttributeException {
        if (email == null || locationId == null) {
            throw new ParamErrorException();
        }

        Customer customer = customerDao.findById(email).orElse(null);
        if (customer == null) {
            //用户不存在
            throw new UserNotExistsException();
        }
        if (customer.getState() != UserState.ACTIVATED) {
            //用户状态不正确
            throw new UnupdatableException(customer.getState());
        }
        if (!customer.hasLocation(locationId)) {
            //没有该地址
            throw new NoSuchAttributeException();
        }

        Location location = locationDao.findById(locationId).orElse(null);
        if (location != null) {
            location.setIsInUse(false);
            locationDao.save(location);
        }
    }

    /**
     * 注销账号
     *
     * @param email    email
     * @param password password
     */
    @Override
    public void closeCustomer(String email, String password) throws ParamErrorException, UserNotExistsException, UnupdatableException, PasswordErrorException {
        if (email == null || password == null) {
            throw new ParamErrorException();
        }
        Customer customer = customerDao.findById(email).orElse(null);
        if (customer == null) {
            //用户不存在
            throw new UserNotExistsException();
        }
        if (customer.getState() != UserState.ACTIVATED) {
            //用户状态不正确
            throw new UnupdatableException(customer.getState());
        }
        if (!password.equals(customer.getPassword())) {
            //密码不正确
            throw new PasswordErrorException();
        }
        //设置账号
        customer.setState(UserState.CLOSED);
        customerDao.save(customer);
    }

    @Override
    public List<Restaurant> getRestaurantWithinDistributionDistance(String email, Integer locationId) throws ParamErrorException, NoSuchAttributeException, UserNotExistsException {
        return customerPlaceHelper.getRestaurantWithinDistributionDistance(email, locationId);
    }

    /**
     * @param email      email
     * @param locationId locationId
     * @param keyWord    搜索关键字
     * @return
     * @throws ParamErrorException      传入参数为null
     * @throws NoSuchAttributeException 没有该地址
     * @throws UserNotExistsException   没有该customer
     */
    @Override
    public List<Restaurant> getSearchedRestaurant(String email, Integer locationId, String keyWord) throws ParamErrorException, UserNotExistsException, NoSuchAttributeException {
        return customerPlaceHelper.getSearchedRestaurant(email, locationId, keyWord);
    }

    /**
     * 充值
     *
     * @param customerEmail email
     * @param amount        数值
     */
    @Override
    public void recharge(String customerEmail, Double amount) throws ParamErrorException, UserNotExistsException, UnupdatableException {
        if (customerEmail == null) {
            throw new ParamErrorException("用户");
        }
        if (amount == null || amount <= 0) {
            throw new ParamErrorException("数额");
        }

        Customer customer = customerDao.find(customerEmail);
        if (customer == null) {
            throw new UserNotExistsException();
        }

        if (!customer.isEnable()) {
            throw new UnupdatableException(customer.getState());
        }

        customer.increaceBalance(amount);
        customerDao.save(customer);
        //添加充值记录
        CustomerRechargeLog log = new CustomerRechargeLog();
        log.setCustomer(customer);
        log.setAmount(amount);
        customerRechargeLogDao.save(log);
    }

    /**
     * 获取统计数据列表
     *
     * @param email email
     * @return
     */
    @Override
    public List<OrderStatisticsInfoVo> getStatisticsInfos(String email) throws UserNotExistsException {
        return customerStatisticsHelper.getStatisticsInfos(email);
    }

    @Override
    public List<Order> getOrdersByRestaurantOfCustomer(String email, Integer rid) throws UserNotExistsException {
        return customerStatisticsHelper.getOrdersByRestaurantOfCustomer(email, rid);
    }

    @Override
    public List<Order> getOrdersByTimeOfCustomer(String email, String time, String timeFormat) throws UserNotExistsException {
        return customerStatisticsHelper.getOrdersByTimeOfCustomer(email, time, timeFormat);
    }
}
