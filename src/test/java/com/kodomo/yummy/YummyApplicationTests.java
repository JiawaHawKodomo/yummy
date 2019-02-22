package com.kodomo.yummy;

import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.dao.CustomerDao;
import com.kodomo.yummy.dao.OrderSettlementStrategyDao;
import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.entity.OrderSettlementStrategy;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.util.Utility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YummyApplicationTests {

    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private RestaurantDao restaurantDao;
    @Autowired
    private OrderSettlementStrategyDao orderSettlementStrategyDao;
    @Autowired
    private OrderBlService orderBlService;

    @Resource
    private EntityManager entityManager;

    @Test
    public void contextLoads() {
        List<Restaurant> restaurants = restaurantDao.getRestaurantWithinSquare(35.60413, 115.24712, 5.0);
        System.out.println(restaurants.size());
    }
}

