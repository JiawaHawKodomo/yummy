package com.kodomo.yummy.bl.customer;

import com.kodomo.yummy.config.StaticConfig;
import com.kodomo.yummy.dao.CustomerDao;
import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.entity.Customer;
import com.kodomo.yummy.entity.Location;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.exceptions.NoSuchAttributeException;
import com.kodomo.yummy.exceptions.ParamErrorException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 点餐时需要的业务逻辑, 主要为计算
 *
 * @author Shuaiyu Yao
 * @create 2019-02-22 17:44
 */
@Component
public class CustomerPlaceBlService {

    private final CustomerDao customerDao;
    private final RestaurantDao restaurantDao;

    @Autowired
    public CustomerPlaceBlService(RestaurantDao restaurantDao, CustomerDao customerDao) {
        this.restaurantDao = restaurantDao;
        this.customerDao = customerDao;
    }

    /**
     * 获取配送范围内的餐厅
     *
     * @param email 用户email
     * @return
     */
    @NotNull
    List<Restaurant> getRestaurantWithinDistributionDistance(String email, Integer locationId) throws ParamErrorException, UserNotExistsException, NoSuchAttributeException {
        if (email == null) {
            throw new ParamErrorException("用户邮箱");
        }

        Customer customer = customerDao.findById(email).orElse(null);
        if (customer == null) {
            throw new UserNotExistsException();//customer不存在
        }

        Location location = customer.getLocationById(locationId);
        if (location == null) {
            throw new NoSuchAttributeException();//location不存在
        }

        return restaurantDao.getRestaurantWithinSquare(location.getLat(), location.getLng(), StaticConfig.getMaxDistributionDistance())
                .stream().filter(r -> r.getState() == UserState.ACTIVATED)//有效餐厅
                .filter(r -> location.distanceBetween(r.getLocation()) <= StaticConfig.getMaxDistributionDistance())//位置在距离内
                .sorted(Comparator.comparingDouble(a -> a.getLocation().distanceBetween(location)))
                .collect(Collectors.toList());
    }

    /**
     * @param email      email
     * @param locationId locationId
     * @param keyWord    搜索关键字
     * @return
     * @throws ParamErrorException      传入参数为null
     * @throws NoSuchAttributeException 没有该地址
     * @throws UserNotExistsException   没有该customer
     */
    @NotNull
    List<Restaurant> getSearchedRestaurant(String email, Integer locationId, String keyWord) throws ParamErrorException, UserNotExistsException, NoSuchAttributeException {
        if (keyWord == null)
            return getRestaurantWithinDistributionDistance(email, locationId);
        return getRestaurantWithinDistributionDistance(email, locationId).stream()
                .filter(restaurant -> restaurant.isMatched(keyWord) != null)
                .sorted((a, b) -> b.isMatched(keyWord).length() - a.isMatched(keyWord).length())
                .collect(Collectors.toList());
    }
}
