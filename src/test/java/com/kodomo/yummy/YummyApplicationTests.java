package com.kodomo.yummy;

import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.dao.CustomerDao;
import com.kodomo.yummy.dao.OrderSettlementStrategyDao;
import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.entity.Location;
import com.kodomo.yummy.util.AddressGetter;
import com.kodomo.yummy.util.Utility;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
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

    @Autowired
    private AddressGetter addressGetter;

    @Test
    @Ignore
    public void contextLoads() {
        System.out.println(restaurantDao.findById(Utility.integer(null)).orElse(null));
    }

    @Test
    public void testForTx() {
        List<Location> locations = addressGetter.getLocations();
        System.out.println(locations);
    }
}

