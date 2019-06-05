package com.kodomo.yummy.util.testdata;

import com.kodomo.yummy.dao.OfferingDao;
import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.entity.restaurant.Offering;
import com.kodomo.yummy.entity.restaurant.OfferingType;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-06-04 14:09
 */
@Component
public class OfferingInjector {

    @Value("${testdata.restaurant.offering.typeSize}")
    private Integer offeringTypeSize;
    @Value("${testdata.restaurant.offering.maxPrice}")
    private Integer offeringMaxPrice;
    @Value("${testdata.restaurant.offering.offeringRemaining}")
    private Integer offeringRemaining;
    @Value("${testdata.restaurant.offering.offeringSize}")
    private Integer offeringSize;
    private final RestaurantDao restaurantDao;
    private final RandomHelper randomHelper;
    private final OfferingDao offeringDao;

    @Autowired
    public OfferingInjector(RestaurantDao restaurantDao, RandomHelper randomHelper, OfferingDao offeringDao) {
        this.restaurantDao = restaurantDao;
        this.randomHelper = randomHelper;
        this.offeringDao = offeringDao;
    }

    void injectTypeAndOffering(List<Restaurant> restaurants) {
        //type
        injectOfferingTypes(restaurants);
        //offerings
        injectOfferings(restaurants);
    }

    private void injectOfferingTypes(List<Restaurant> restaurants) {
        for (Restaurant restaurant : restaurants) {
            Set<OfferingType> types = new HashSet<>();
            for (int i = 0; i < offeringTypeSize; i++) {
                types.add(randomType(i));
            }

            restaurant.setOfferingTypes(types);
        }
        restaurantDao.saveAll(restaurants);
    }

    private void injectOfferings(List<Restaurant> restaurants) {
        List<Offering> offerings = new LinkedList<>();
        for (Restaurant restaurant : restaurants) {
            for (int i = 0; i < offeringSize; i++) {
                offerings.add(randomOffering(restaurant));
            }
        }

        offeringDao.saveAll(offerings);
    }

    private OfferingType randomType(int index) {
        OfferingType type = new OfferingType();
        type.setName("类型" + index);
        type.setSequenceNumber(index);
        return type;
    }

    private Offering randomOffering(Restaurant restaurant) {
        Offering offering = new Offering();
        offering.setName("菜品" + randomHelper.randomIndex(1000));
        offering.setNote("这是" + offering.getName());
        offering.setPrice((double) randomHelper.randomInt(offeringMaxPrice));

        //remaining
        if (randomHelper.randomBoolean()) {
            offering.setRemainingNumber(offeringRemaining);
        }
        offering.setRestaurant(restaurant);
        offering.setOfferingTypes(randomHelper.randomCollection(restaurant.getOfferingTypes()));
        return offering;
    }

}
