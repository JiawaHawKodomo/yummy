package com.kodomo.yummy.bl.restaurant;

import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.exceptions.DuplicatedPrimaryKeyException;
import com.kodomo.yummy.exceptions.ParamErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-17 17:05
 */
@Service
public class RestaurantBlServiceImpl implements RestaurantBlService {

    private final RestaurantCreator restaurantCreator;
    private final RestaurantDao restaurantDao;

    @Autowired
    public RestaurantBlServiceImpl(RestaurantCreator restaurantCreator, RestaurantDao restaurantDao) {
        this.restaurantCreator = restaurantCreator;
        this.restaurantDao = restaurantDao;
    }

    @Override
    public Restaurant registerRestaurant(String name, String password, String tel, String time, String type, String note, String city, Double lat, Double lng, String block, String point, String addressNote) throws ParamErrorException, DuplicatedPrimaryKeyException {
        return restaurantCreator.createNewRestaurantForDatabase(name, password, tel, time, type, note, city, lat, lng, block, point, addressNote);
    }

    @Override
    public Restaurant login(String id, String password) {
        if (id == null || password == null || id.equals("") || password.equals(""))
            return null;

        try {
            Integer idInteger = Integer.valueOf(id);
            //从数据库中找餐厅信息
            Restaurant restaurant = restaurantDao.findById(idInteger).orElse(null);
            if (password.equals(restaurant.getPassword())) {
                return restaurant;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 按状态获取餐厅信息
     *
     * @param state state
     * @return list
     */
    @Override
    public List<Restaurant> getRestaurantByState(UserState state) {
        if (state == null) {
            return new ArrayList<>();
        }

        Restaurant example = new Restaurant();
        example.setState(state);
        try {
            return restaurantDao.findAll(Example.of(example), Sort.by(Sort.Direction.ASC, "registerTime"));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
