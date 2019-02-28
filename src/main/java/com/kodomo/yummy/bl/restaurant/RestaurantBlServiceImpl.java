package com.kodomo.yummy.bl.restaurant;

import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.controller.vo.*;
import com.kodomo.yummy.dao.OfferingDao;
import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.entity.Offering;
import com.kodomo.yummy.entity.OfferingType;
import com.kodomo.yummy.entity.Order;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-17 17:05
 */
@Service
public class RestaurantBlServiceImpl implements RestaurantBlService {

    private final RestaurantCreator restaurantCreator;
    private final RestaurantDao restaurantDao;
    private final OfferingDao offeringDao;
    private final OfferingCreator offeringCreator;
    private final RestaurantStrategyBlService restaurantStrategyBlService;
    private final RestaurantStatisticsHelper restaurantStatisticsHelper;

    @Autowired
    public RestaurantBlServiceImpl(RestaurantCreator restaurantCreator, RestaurantDao restaurantDao, OfferingDao offeringDao, OfferingCreator offeringCreator, RestaurantStrategyBlService restaurantStrategyBlService, RestaurantStatisticsHelper restaurantStatisticsHelper) {
        this.restaurantCreator = restaurantCreator;
        this.restaurantDao = restaurantDao;
        this.offeringDao = offeringDao;
        this.offeringCreator = offeringCreator;
        this.restaurantStrategyBlService = restaurantStrategyBlService;
        this.restaurantStatisticsHelper = restaurantStatisticsHelper;
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

    /**
     * id查询餐厅
     *
     * @param id
     * @return
     */
    @Override
    public Restaurant getRestaurantById(Integer id) {
        if (id == null) return null;
        return restaurantDao.findById(id).orElse(null);
    }

    /**
     * 修改餐厅的餐品类型
     */
    @Override
    public void updateRestaurantOfferingType(Integer rid, List<OfferingTypeVo> newTypes) throws ParamErrorException, UserNotExistsException, DuplicatedPrimaryKeyException, UnupdatableException {
        if (rid == null || newTypes == null) {
            throw new ParamErrorException();//参数错误
        }

        Restaurant restaurant = restaurantDao.findById(rid).orElse(null);
        if (restaurant == null) {
            throw new UserNotExistsException();//餐厅实体不存在
        }

        if (restaurant.getState() != UserState.ACTIVATED) {
            //状态不对
            throw new UnupdatableException(restaurant.getState());
        }

        //处理新set
        Set<OfferingType> newSet = new HashSet<>();
        for (int i = 0; i < newTypes.size(); i++) {
            OfferingTypeVo vo = newTypes.get(i);
            if (vo.getName() == null || vo.getName().equals("")) {
                throw new ParamErrorException();
            }

            OfferingType oldType = restaurant.getOfferingTypeById(vo.getId());
            if (oldType == null) {
                OfferingType type = new OfferingType();
                type.setName(vo.getName());
                type.setSequenceNumber(i);
                newSet.add(type);
            } else {
                oldType.setName(vo.getName());
                oldType.setSequenceNumber(i);
                newSet.add(oldType);
            }
        }

        restaurant.setOfferingTypes(newSet);
        try {
            restaurantDao.save(restaurant);
        } catch (Exception e) {
            throw new DuplicatedPrimaryKeyException();
        }
    }

    /**
     * 保存餐品信息
     *
     * @param rid
     * @param vo
     */
    @Override
    public void saveOffering(Integer rid, OfferingVo vo) throws ParamErrorException, UserNotExistsException, UnupdatableException {
        if (rid == null || vo == null) {
            throw new ParamErrorException();
        }

        Restaurant restaurant = restaurantDao.findById(rid).orElse(null);
        if (restaurant == null) {
            throw new UserNotExistsException();//用户不存在
        }

        if (restaurant.getState() != UserState.ACTIVATED) {
            throw new UnupdatableException(restaurant.getState());//状态不对
        }

        Offering newOffering = offeringCreator.getNewOfferingForDatabase(vo, restaurant);
        try {
            offeringDao.save(newOffering);
            //处理旧餐品信息
            if (vo.getId() != null) {
                Offering oldOffering = offeringDao.findById(vo.getId()).orElse(null);
                if (oldOffering != null) {
                    oldOffering.setEndTime(new Date());
                    offeringDao.save(oldOffering);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除商品信息
     *
     * @param rid
     * @param offeringId
     */
    @Override
    public void deleteOffering(Integer rid, Integer offeringId) throws ParamErrorException, NoSuchAttributeException, UnupdatableException {
        if (rid == null || offeringId == null) {
            throw new ParamErrorException("餐厅, 商品");
        }

        Offering offering = offeringDao.findById(offeringId).orElse(null);
        if (offering == null) {
            throw new NoSuchAttributeException();//没有该商品
        }

        if (offering.getRestaurantId() == null || !offering.getRestaurantId().equals(rid)) {
            throw new UnupdatableException();//拥有商品的餐厅不是该餐厅
        }

        //设置结束时间, 数据库中保留
        offering.setEndTime(new Date());
        offeringDao.save(offering);
    }

    /**
     * 添加满减策略
     *
     * @param rid rid
     * @param vos vos
     */
    @Override
    public void addRestaurantStrategy(Integer rid, List<RestaurantStrategyVo> vos) throws ParamErrorException, UserNotExistsException {
        restaurantStrategyBlService.addRestaurantStrategy(rid, vos);
    }

    /**
     * 删除满减策略
     *
     * @param rid
     * @param strategyId
     * @throws ParamErrorException      参数为null
     * @throws NoSuchAttributeException 没有该策略
     * @throws UnupdatableException     不是自己的策略, 无法修改
     */
    @Override
    public void deleteRestaurantStrategy(Integer rid, Integer strategyId) throws NoSuchAttributeException, ParamErrorException, UnupdatableException {
        restaurantStrategyBlService.deleteRestaurantStrategy(rid, strategyId);
    }

    /**
     * 计算获得统计信息
     *
     * @param rid rid
     * @return
     */
    @Override
    public List<OrderStatisticsInfoVo> getStatisticsInfos(Integer rid) {
        return restaurantStatisticsHelper.getStatisticsInfos(rid);
    }

    @Override
    public List<Order> getOrdersByTimeOfCustomer(Integer rid, String time, String format) {
        return restaurantStatisticsHelper.getOrdersByTimeOfCustomer(rid, time, format);
    }

    public List<RestaurantStatisticsVo> getRestaurantStatisticsInfo() {
        return restaurantStatisticsHelper.getRestaurantStatisticsInfo();
    }
}
