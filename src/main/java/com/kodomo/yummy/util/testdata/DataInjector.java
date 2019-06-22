package com.kodomo.yummy.util.testdata;

import com.kodomo.yummy.entity.customer.Customer;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Scanner;


/**
 * @author Shuaiyu Yao
 * @create 2019-06-03 20:09
 */
@Slf4j
@Component
@org.springframework.core.annotation.Order(100)
@EnableTransactionManagement
public class DataInjector implements ApplicationRunner {

    private final RestaurantInjector restaurantInjector;
    private final OfferingInjector offeringInjector;
    private final CustomerInjector customerInjector;
    private final OrderInjector orderInjector;

    @Autowired
    public DataInjector(RestaurantInjector restaurantInjector, OfferingInjector offeringInjector, CustomerInjector customerInjector, OrderInjector orderInjector) {
        this.restaurantInjector = restaurantInjector;
        this.offeringInjector = offeringInjector;
        this.customerInjector = customerInjector;
        this.orderInjector = orderInjector;
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    //@Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (args.getNonOptionArgs().contains("testdata")) {
            System.out.println("确定要注入测试数据么? 输入Y/y执行.");
            Scanner scanner = new Scanner(System.in);
            String s = scanner.nextLine();
            if ("Y".equals(s) || "y".equals(s)) {
                //注入开始
                log.info("数据库注入开始");
                log.info("------------");

                //商家信息
                injectRestaurantData();
                //用户信息
                List<Customer> customers = injectCustomerData();
                //订单信息
                injectOrderData(customers);

                log.info("------------");
                log.info("数据库注入结束");
            }
            scanner.close();
        }
    }

    //@Transactional
    private void injectRestaurantData() {
        log.info("正在注入商家信息");
        List<Restaurant> restaurants = restaurantInjector.injectRestaurants();
        offeringInjector.injectTypeAndOffering(restaurants);
        log.info("共注入" + restaurants.size() + "个商家");
    }

    private List<Customer> injectCustomerData() {
        log.info("正在注入消费者信息");
        List<Customer> customers = customerInjector.injectCustomers();
        log.info("共注入" + customers.size() + "个消费者");
        return customers;

    }

    private void injectOrderData(List<Customer> customers) {
        log.info("正在注入订单信息");
        int num = orderInjector.injectOrders(customers);
        log.info("共注入" + num + "个订单");
    }

}
