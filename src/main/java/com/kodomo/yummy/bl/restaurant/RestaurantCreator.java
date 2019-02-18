package com.kodomo.yummy.bl.restaurant;

import com.kodomo.yummy.bl.util.ValidatingHelper;
import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.dao.RestaurantTypeDao;
import com.kodomo.yummy.entity.Location;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.entity.RestaurantType;
import com.kodomo.yummy.entity.UserState;
import com.kodomo.yummy.exceptions.ParamErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-17 17:07
 */
@Service
public class RestaurantCreator {

    private final ValidatingHelper validatingHelper;
    private final RestaurantDao restaurantDao;
    private final RestaurantTypeDao restaurantTypeDao;

    @Autowired
    public RestaurantCreator(ValidatingHelper validatingHelper, RestaurantDao restaurantDao, RestaurantTypeDao restaurantTypeDao) {
        this.validatingHelper = validatingHelper;
        this.restaurantDao = restaurantDao;
        this.restaurantTypeDao = restaurantTypeDao;
    }

    /**
     * 创建Restaurant实体对象, 检查合法性
     *
     * @param name        name
     * @param tel         tel
     * @param time        time
     * @param type        type
     * @param note        note
     * @param city        city
     * @param lat         lat
     * @param lng         lng
     * @param block       block
     * @param point       point
     * @param addressNote addressNote
     * @return restaurant对象
     */
    Restaurant createNewRestaurantForDatabase(String name, String password, String tel, String time, String type, String note, String city, Double lat, Double lng, String block, String point, String addressNote) throws ParamErrorException {
        StringBuilder errorInfo = new StringBuilder();
        if (name == null || name.equals("")) {
            errorInfo.append("名称填写错误").append(System.lineSeparator());
        }
        if (password == null || password.equals("")) {
            errorInfo.append("密码格式错误").append(System.lineSeparator());
        }
        if (!validatingHelper.isTelephone(tel)) {
            errorInfo.append("电话号码填写错误").append(System.lineSeparator());
        }
        Time[] times = new Time[0];
        try {
            times = formatTimes(time);
        } catch (Exception e) {
            errorInfo.append("营业时间填写错误").append(System.lineSeparator());
        }
        if (type == null || type.equals("")) {
            errorInfo.append("类别填写错误").append(System.lineSeparator());
        }
        if (city == null || city.equals("") || lat == null || lng == null || block == null || block.equals("") || point == null || point.equals("")) {
            errorInfo.append("地点选择错误").append(System.lineSeparator());
        }

        if (!errorInfo.toString().equals("")) {
            //有异常
            throw new ParamErrorException(errorInfo.toString());
        }

        //填装信息
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setPassword(password);
        restaurant.setTelephone(tel);
        restaurant.setRunFrom(times[0]);
        restaurant.setRunTo(times[1]);
        restaurant.setState(UserState.UNACTIVATED);

        //检查餐厅类型
        RestaurantType type1 = new RestaurantType();
        type1.setContent(type);
        List<RestaurantType> types = restaurantTypeDao.findAll(Example.of(type1));
        Set<RestaurantType> set = new HashSet<>();
        if (types.isEmpty()) {
            set.add(type1);
        } else {
            set.addAll(types);
        }
        restaurant.setTypes(set);
        restaurant.setNote(note);

        Location location = new Location();
        location.setCity(city);
        location.setLat(lat);
        location.setLng(lng);
        location.setBlockInfo(block);
        location.setPointInfo(point);
        location.setNote(addressNote);
        restaurant.setLocation(location);

        //存储
        try {
            restaurant = restaurantDao.save(restaurant);
        } catch (Exception e) {
            throw new ParamErrorException("该电话号码已注册");
        }
        return restaurant;
    }

    /**
     * 根据字符串返回两个时间
     *
     * @param s
     * @return
     * @throws Exception
     */
    private Time[] formatTimes(String s) throws Exception {
        try {
            String[] tmp = s.split("-");
            String time0 = tmp[0];
            String time1 = tmp[1];
            String[] tmp0 = time0.split(":");
            int t0h = Integer.parseInt(tmp0[0]);
            int t0m = Integer.parseInt(tmp0[1]);
            String[] tmp1 = time1.split(":");
            int t1h = Integer.parseInt(tmp1[0]);
            int t1m = Integer.parseInt(tmp1[1]);
            if (t0h > 23 || t1h > 23 || t0m > 59 || t1m > 59)
                throw new Exception();
            Time r0 = Time.valueOf(tmp[0] + ":00");
            Time r1 = Time.valueOf(tmp[1] + ":00");
            if (r0.after(r1))
                throw new Exception();
            return new Time[]{r0, r1};
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

}
