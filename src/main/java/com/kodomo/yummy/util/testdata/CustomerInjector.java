package com.kodomo.yummy.util.testdata;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.dao.CustomerDao;
import com.kodomo.yummy.entity.Location;
import com.kodomo.yummy.entity.customer.Customer;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.exceptions.ParamErrorException;
import com.kodomo.yummy.exceptions.UnupdatableException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import com.kodomo.yummy.util.AddressGetter;
import com.kodomo.yummy.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Shuaiyu Yao
 * @create 2019-06-04 16:01
 */
@Slf4j
@Component
public class CustomerInjector {

    @Value("${testdata.customer.size}")
    private Integer customerSize;
    @Value("${testdata.customer.registerStartingTime}")
    private String registerStartingTime;
    @Value("${testdata.customer.registerEndingTime}")
    private String registerEndingTime;
    @Value("${testdata.customer.password}")
    private String password;
    private final RandomHelper randomHelper;
    private final AddressGetter addressGetter;
    private final CustomerDao customerDao;

    @Autowired
    public CustomerInjector(RandomHelper randomHelper, AddressGetter addressGetter, CustomerDao customerDao) {
        this.randomHelper = randomHelper;
        this.addressGetter = addressGetter;
        this.customerDao = customerDao;
    }

    List<Customer> injectCustomers() {
        //customer
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < customerSize; i++) {
            customers.add(randomCustomer());
        }

        customerDao.saveAll(customers);
        log.info("用户数据注入完成");

        //recharge
//        for (Customer customer : customers) {
//            try {
//                customerBlService.recharge(customer.getEmail(), rechargeAmount);
//            } catch (ParamErrorException | UserNotExistsException | UnupdatableException e) {
//                e.printStackTrace();
//            }
//        }

        customers = customerDao.findAllById(customers.stream().map(Customer::getEmail).collect(Collectors.toList()));
        return customers;
    }

    private Customer randomCustomer() {
        Customer customer = new Customer();
        customer.setEmail(randomHelper.randomEmail());
        customer.setPassword(Utility.getSHA256Str(password));
        customer.setName("顾客" + randomHelper.randomIndex(10000));
        customer.setTelephone(randomHelper.randomTelephone());
        customer.setState(UserState.ACTIVATED);
        try {
            customer.setRegisterTime(randomHelper.randomTime(Utility.parseTime(registerStartingTime),
                    Utility.parseTime(registerEndingTime)));
        } catch (ParseException e) {
            log.error("顾客注册时间错误");
            System.exit(0);
        }
        List<Location> locations = new ArrayList<>();
        locations.add(addressGetter.getRandomLocation(customer.getTelephone()));
        customer.setLocations(locations);
        return customer;
    }

}
