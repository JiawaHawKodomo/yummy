package com.kodomo.yummy.bl.restaurant;

import com.kodomo.yummy.bl.location.LocationHelper;
import com.kodomo.yummy.bl.util.ValidatingHelper;
import com.kodomo.yummy.controller.vo.RestaurantModificationVo;
import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.dao.RestaurantModificationInfoDao;
import com.kodomo.yummy.dao.RestaurantTypeDao;
import com.kodomo.yummy.entity.Location;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.entity.RestaurantModificationInfo;
import com.kodomo.yummy.entity.RestaurantType;
import com.kodomo.yummy.entity.entity_enum.RestaurantModificationState;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-17 17:07
 */
@Service
public class RestaurantEntityHelper {

    private final ValidatingHelper validatingHelper;
    private final RestaurantDao restaurantDao;
    private final RestaurantTypeDao restaurantTypeDao;
    private final LocationHelper locationHelper;
    private final RestaurantModificationInfoDao restaurantModificationInfoDao;

    @Autowired
    public RestaurantEntityHelper(ValidatingHelper validatingHelper, RestaurantDao restaurantDao, RestaurantTypeDao restaurantTypeDao, LocationHelper locationHelper, RestaurantModificationInfoDao restaurantModificationInfoDao) {
        this.validatingHelper = validatingHelper;
        this.restaurantDao = restaurantDao;
        this.restaurantTypeDao = restaurantTypeDao;
        this.locationHelper = locationHelper;
        this.restaurantModificationInfoDao = restaurantModificationInfoDao;
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
    Restaurant createNewRestaurantForDatabase(String name, String password, String tel, String time, String type, String note, String city, Double lat, Double lng, String block, String point, String addressNote) throws ParamErrorException, DuplicatedUniqueKeyException {
        List<String> errorField = new ArrayList<>();
        if (name == null || name.equals("")) {
            errorField.add("名称");
        }
        if (password == null || password.equals("")) {
            errorField.add("密码");
        }
        if (!validatingHelper.isTelephone(tel)) {
            errorField.add("电话号码");
        }
        Time[] times = new Time[0];
        try {
            times = formatTimes(time);
        } catch (Exception e) {
            errorField.add("营业时间");
        }
        if (type == null || type.equals("")) {
            errorField.add("类别");
        }
        if (city == null || city.equals("") || lat == null || lng == null || block == null || block.equals("") || point == null || point.equals("")) {
            errorField.add("地点");
        }

        if (!errorField.isEmpty()) {
            //有异常
            throw new ParamErrorException(errorField);
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
        RestaurantType type1 = restaurantTypeDao.findTypeByContent(type);
        Set<RestaurantType> set = new HashSet<>();
        dealWithType(type1, set, type);
        restaurant.setTypes(set);
        restaurant.setNote(note);

        Location location = locationHelper.createLocation(block, point, note, city, tel, lat, lng);
        restaurant.setLocation(location);

        //存储
        try {
            restaurant = restaurantDao.save(restaurant);
        } catch (Exception e) {
            throw new DuplicatedUniqueKeyException("该电话号码已注册");
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

    /**
     * 保存餐厅修改信息请求
     *
     * @param vo
     * @param restaurantId
     * @throws ParamErrorException
     * @throws UserNotExistsException
     * @throws DuplicatedUniqueKeyException
     */
    void submitModification(RestaurantModificationVo vo, Integer restaurantId) throws ParamErrorException, UserNotExistsException, DuplicatedUniqueKeyException, DuplicatedSubmitException {
        if (vo == null || restaurantId == null) {
            throw new ParamErrorException();
        }

        //restaurant
        Restaurant restaurant = restaurantDao.find(restaurantId);
        if (restaurant == null) {
            throw new UserNotExistsException();
        }

        //已有申请
        if (restaurant.getWaitingConfirmModificationInfo() != null) {
            throw new DuplicatedSubmitException();
        }

        List<String> errors = new ArrayList<>();
        if (vo.getName() == null) {
            errors.add("名字");
        }
        Time[] times = new Time[0];
        try {
            times = formatTimes(vo.getBusinessHours());
        } catch (Exception e) {
            errors.add("营业时间");
        }

        if (vo.getTelephone() == null || !validatingHelper.isTelephone(vo.getTelephone())) {
            errors.add("电话");
        }

        if (vo.getTypes() == null || vo.getTypes().isEmpty()) {
            errors.add("餐厅类型");
        }
        //检查填写异常
        if (!errors.isEmpty()) {
            throw new ParamErrorException(errors);
        }

        //手机号已存在
        if (!vo.getTelephone().equals(restaurant.getTelephone()) && restaurantDao.getRestaurantByTelephone(vo.getTelephone()) != null) {
            throw new DuplicatedUniqueKeyException();
        }

        RestaurantModificationInfo modificationInfo = new RestaurantModificationInfo();
        modificationInfo.setRestaurant(restaurant);
        modificationInfo.setName(vo.getName());
        modificationInfo.setTelephone(vo.getTelephone());
        modificationInfo.setRunFrom(times[0]);
        modificationInfo.setRunTo(times[1]);
        modificationInfo.setLocationNote(vo.getLocationNote());
        //处理type
        Set<RestaurantType> types = new HashSet<>();
        vo.getTypes().forEach(s -> {
            RestaurantType type = restaurantTypeDao.findTypeByContent(s);
            dealWithType(type, types, s);
        });
        modificationInfo.setTypes(types);

        //保存
        restaurantModificationInfoDao.save(modificationInfo);
    }

    private void dealWithType(RestaurantType type, Set<RestaurantType> types, String content) {
        if (type != null) {
            types.add(type);
        } else {
            //新建type
            RestaurantType newType = new RestaurantType();
            newType.setContent(content);
            types.add(newType);
        }
    }

    /**
     * 查找所有未审核的修改信息
     *
     * @return
     */
    List<RestaurantModificationInfo> getWaitingRestaurantModificationInfo() {
        return restaurantModificationInfoDao.getWaitingModificationInfo();
    }

    /**
     * 处理审核的修改信息
     *
     * @param modificationId
     * @param pass
     */
    void confirmModification(Integer modificationId, Boolean pass) throws ParamErrorException, NoSuchAttributeException, DuplicatedUniqueKeyException {
        if (modificationId == null || pass == null) {
            throw new ParamErrorException();
        }

        RestaurantModificationInfo info = restaurantModificationInfoDao.find(modificationId);
        if (modificationId == null) {//不存在
            throw new NoSuchAttributeException();
        }

        info.setCheckTime(new Date());
        if (!pass) {
            //没通过
            info.setState(RestaurantModificationState.NOT_APPROVED);
        } else {
            //通过
            info.setState(RestaurantModificationState.APPROVED);
            //处理餐厅信息
            modifyRestaurantInfo(info);
        }

        //保存修改信息结果
        restaurantModificationInfoDao.save(info);
    }

    /**
     * 按照申请修改餐厅的信息
     *
     * @param info
     */
    private void modifyRestaurantInfo(RestaurantModificationInfo info) throws DuplicatedUniqueKeyException {
        Restaurant restaurant = info.getRestaurant();
        restaurant.setName(info.getName());
        restaurant.setTelephone(info.getTelephone());
        restaurant.setRunFrom(info.getRunFrom());
        restaurant.setRunTo(info.getRunTo());
        restaurant.setLocationNote(info.getLocationNote());
        restaurant.setTypes(new HashSet<>(info.getTypes()));

        try {
            restaurantDao.save(restaurant);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DuplicatedUniqueKeyException();
        }
    }
}
