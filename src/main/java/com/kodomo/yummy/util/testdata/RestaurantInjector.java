package com.kodomo.yummy.util.testdata;

import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.dao.RestaurantStrategyDao;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import com.kodomo.yummy.entity.restaurant.RestaurantStrategy;
import com.kodomo.yummy.util.AddressGetter;
import com.kodomo.yummy.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import java.sql.Time;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Shuaiyu Yao
 * @create 2019-06-04 12:54
 */
@Slf4j
@Component
public class RestaurantInjector {

    @Value("${testdata.restaurant.size}")
    private Integer restaurantSize;
    @Value("${testdata.restaurant.password}")
    private String password;
    @Value("${testdata.restaurant.registerStartingTime}")
    private String registerStartingTime;
    @Value("${testdata.restaurant.registerEndingTime}")
    private String registerEndingTime;
    private final RandomHelper randomHelper;
    private final AddressGetter addressGetter;
    private final RestaurantDao restaurantDao;
    private final RestaurantStrategyDao restaurantStrategyDao;

    private RestaurantStrategy randomRestaurantStrategies(Restaurant restaurant) {
        RestaurantStrategy strategy = new RestaurantStrategy();
        Random random = new Random();
        int greaterThan = random.nextInt(100) + 1;
        strategy.setGreaterThan((double) greaterThan);
        strategy.setDiscount((double) random.nextInt(greaterThan));
        strategy.setRestaurant(restaurant);
        return strategy;
    }

    @Autowired
    public RestaurantInjector(RandomHelper randomHelper, AddressGetter addressGetter, RestaurantDao restaurantDao, RestaurantStrategyDao restaurantStrategyDao) {
        this.randomHelper = randomHelper;
        this.addressGetter = addressGetter;
        this.restaurantDao = restaurantDao;
        this.restaurantStrategyDao = restaurantStrategyDao;
    }

    @Transactional
    List<Restaurant> injectRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        List<RestaurantStrategy> strategies = new ArrayList<>();
        for (int i = 0; i < restaurantSize; i++) {
            Restaurant restaurant = getRandomRestaurant();
            restaurants.add(restaurant);
        }

        restaurantDao.saveAll(restaurants);
        log.info("餐厅数据注入完毕");

        restaurants = restaurantDao.findAllById(restaurants.stream().map(Restaurant::getRestaurantId).collect(Collectors.toList()));

        for (Restaurant restaurant : restaurants) {
            strategies.add(randomRestaurantStrategies(restaurant));
        }
        restaurantStrategyDao.saveAll(strategies);
        log.info("餐厅策略数据注入完毕");
        return restaurants;
    }

    private Restaurant getRandomRestaurant() {
        Restaurant restaurant = new Restaurant();

        restaurant.setName("餐厅" + randomHelper.randomIndex(10000));
        restaurant.setTelephone(randomHelper.randomTelephone());
        restaurant.setPassword(Utility.getSHA256Str(password));
        try {
            Time[] times = Utility.formatTimes("00:00-23:59");
            restaurant.setRunFrom(times[0]);
            restaurant.setRunTo(times[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        restaurant.setNote("欢迎光临" + restaurant.getName());
        restaurant.setState(UserState.ACTIVATED);
        restaurant.setLocation(addressGetter.getRandomLocation());
        try {
            restaurant.setRegisterTime(randomHelper.randomTime(Utility.parseTime(registerStartingTime),
                    Utility.parseTime(registerEndingTime)));
        } catch (ParseException e) {
            log.error("餐厅注册时间错误");
            System.exit(0);
        }
        return restaurant;
    }

}
