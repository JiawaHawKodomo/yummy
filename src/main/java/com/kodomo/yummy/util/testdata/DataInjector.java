package com.kodomo.yummy.util.testdata;

import com.kodomo.yummy.entity.customer.Customer;
import com.kodomo.yummy.entity.order.Order;
import com.kodomo.yummy.entity.restaurant.Offering;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author Shuaiyu Yao
 * @create 2019-06-03 20:09
 */
@Slf4j
@Component
public class DataInjector implements ApplicationRunner {

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (args.getNonOptionArgs().contains("testdata")) {
            //注入开始
            log.info("数据库注入开始");
            log.info("------------");

            //商家信息
            List<Restaurant> restaurants = injectRestaurantData();
            //菜品种类及菜品信息
            List<Offering> offerings = injectOfferingData();
            //用户信息
            List<Customer> customers = injectCustomerData();
            //订单信息
            List<Order> orders = injectOrderData();

            log.info("------------");
            log.info("数据库注入结束");
        }
    }

    private List<Restaurant> injectRestaurantData() {
        return null;
    }

    private List<Offering> injectOfferingData() {
        return null;
    }

    private List<Customer> injectCustomerData() {
        return null;
    }

    private List<Order> injectOrderData() {
        return null;
    }

}
