package com.kodomo.yummy.bl.restaurant;

import com.kodomo.yummy.controller.vo.RestaurantStrategyVo;
import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.dao.RestaurantStrategyDao;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.entity.RestaurantStrategy;
import com.kodomo.yummy.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-22 15:42
 */
@Component
public class RestaurantStrategyBlService {

    private final RestaurantStrategyDao restaurantStrategyDao;
    private final RestaurantDao restaurantDao;

    @Autowired
    public RestaurantStrategyBlService(RestaurantDao restaurantDao, RestaurantStrategyDao restaurantStrategyDao) {
        this.restaurantDao = restaurantDao;
        this.restaurantStrategyDao = restaurantStrategyDao;
    }

    /**
     * 添加满减策略
     *
     * @param rid rid
     * @param vos vos
     */
    void addRestaurantStrategy(Integer rid, List<RestaurantStrategyVo> vos) throws ParamErrorException, UserNotExistsException, DatabaseUnknownException {
        if (rid == null || vos == null) {
            throw new ParamErrorException();
        }

        Restaurant restaurant = restaurantDao.findById(rid).orElse(null);
        if (restaurant == null) {
            throw new UserNotExistsException();
        }

        for (RestaurantStrategyVo vo : vos) {
            RestaurantStrategy newStrategy = createRestaurantStrategyForDatabase(vo, restaurant);
            try {
                restaurantStrategyDao.save(newStrategy);
            } catch (Exception e) {
                throw new DatabaseUnknownException(e);
            }
        }
    }

    /**
     * 删除满减策略
     *
     * @param rid
     * @param strategyId
     * @throws ParamErrorException      参数为null
     * @throws NoSuchAttributeException 没有该策略
     * @throws UnupdatableException     不是自己的策略, 无法修改
     * @throws DatabaseUnknownException 数据库 错误
     */
    void deleteRestaurantStrategy(Integer rid, Integer strategyId) throws ParamErrorException, NoSuchAttributeException, UnupdatableException, DatabaseUnknownException {
        if (rid == null || strategyId == null) {
            throw new ParamErrorException();
        }

        RestaurantStrategy strategy = restaurantStrategyDao.findById(strategyId).orElse(null);
        if (strategy == null) {
            throw new NoSuchAttributeException();//没有该策略
        }
        if (!rid.equals(strategy.getRestaurantId())) {
            throw new UnupdatableException();//不是自己的策略, 无法修改
        }

        strategy.setEndDate(new Date());//使时间结束
        try {
            restaurantStrategyDao.save(strategy);
        } catch (Exception e) {
            throw new DatabaseUnknownException(e);
        }
    }

    /**
     * 生成实体对象
     *
     * @param vo
     * @param restaurant
     * @return
     * @throws ParamErrorException
     */
    private RestaurantStrategy createRestaurantStrategyForDatabase(RestaurantStrategyVo vo, Restaurant restaurant) throws ParamErrorException {
        if (vo == null || restaurant == null) {
            throw new ParamErrorException();
        }

        //验证
        validateRestaurantStrategy(vo);

        RestaurantStrategy newStrategy = new RestaurantStrategy();
        newStrategy.setGreaterThan(vo.getGreaterThan());
        newStrategy.setDiscount(vo.getDiscount());
        newStrategy.setStartDate(new Date());
        newStrategy.setRestaurant(restaurant);
        return newStrategy;
    }

    /**
     * 验证vo的正确性
     *
     * @param vo
     * @throws ParamErrorException
     */
    private void validateRestaurantStrategy(RestaurantStrategyVo vo) throws ParamErrorException {
        if (vo == null) {
            throw new ParamErrorException();
        }

        List<String> errorField = new ArrayList<>();
        if (vo.getGreaterThan() == null || vo.getGreaterThan() <= 0) {
            errorField.add("满足金额");
        }
        if (vo.getDiscount() == null || vo.getDiscount() <= 0) {
            errorField.add("折扣金额");
        }
        if (!errorField.isEmpty()) {
            throw new ParamErrorException(errorField);
        }
    }
}
