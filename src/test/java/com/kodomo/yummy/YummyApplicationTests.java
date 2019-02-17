package com.kodomo.yummy;

import com.kodomo.yummy.dao.CustomerDao;
import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.entity.Restaurant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YummyApplicationTests {

    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private RestaurantDao restaurantDao;

    @Test
    public void contextLoads() {
        Restaurant restaurant = restaurantDao.findById(1000000).orElse(null);
        System.out.println(restaurant.getTypes().size());
    }
}

