package com.kodomo.yummy.util.testdata;

import com.kodomo.yummy.bl.restaurant.OfferingHelper;
import com.kodomo.yummy.controller.vo.OfferingVo;
import com.kodomo.yummy.dao.OfferingDao;
import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.entity.restaurant.Offering;
import com.kodomo.yummy.entity.restaurant.OfferingType;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

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
    private final OfferingHelper offeringHelper;

    @Autowired
    public OfferingInjector(RestaurantDao restaurantDao, RandomHelper randomHelper, OfferingDao offeringDao, OfferingHelper offeringHelper) {
        this.restaurantDao = restaurantDao;
        this.randomHelper = randomHelper;
        this.offeringDao = offeringDao;
        this.offeringHelper = offeringHelper;
    }

    void injectTypeAndOffering(List<Restaurant> restaurants) {
        //type
        injectOfferingTypes(restaurants);
        //offerings
        injectOfferings(restaurants);
    }

    private void injectOfferingTypes(List<Restaurant> restaurants) {
        for (Restaurant restaurant : restaurants) {
            List<String> typeNames = randomHelper.randomOfferingTypeNames(offeringTypeSize);
            Set<OfferingType> types = new HashSet<>();
            for (int i = 0; i < typeNames.size(); i++) {
                OfferingType type = new OfferingType();
                type.setName(typeNames.get(i));
                type.setSequenceNumber(i);
                types.add(type);
            }

            restaurant.setOfferingTypes(types);
        }
        restaurantDao.saveAll(restaurants);
    }

    private void injectOfferings(List<Restaurant> restaurants) {
        //更新restaurants
        restaurants = restaurantDao.findAllById(restaurants.stream().map(Restaurant::getRestaurantId).collect(Collectors.toList()));
        List<Offering> offerings = new LinkedList<>();
        for (Restaurant restaurant : restaurants) {
            for (int i = 0; i < offeringSize; i++) {
                offerings.add(randomOffering(restaurant));
            }
        }

        offeringDao.saveAll(offerings);
    }

    private OfferingVo randomOfferingVo(Restaurant restaurant) {
        OfferingVo vo = new OfferingVo();
        vo.setName(randomHelper.randomOfferingName());
        vo.setNote("这是" + vo.getName());
        vo.setPrice((double) randomHelper.randomInt(offeringMaxPrice));
        if (randomHelper.randomBoolean()) {
            vo.setRemaining(offeringRemaining);
        }

        vo.setTypes(randomHelper.randomCollection(restaurant.getOfferingTypes().stream().map(OfferingType::getOfferingTypeId).collect(Collectors.toList())));
        return vo;
    }

    private Offering randomOffering(Restaurant restaurant) {
        Offering offering = new Offering();
        offering.setName(randomHelper.randomOfferingName());
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
