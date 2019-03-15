package com.kodomo.yummy.bl.restaurant;

import com.kodomo.yummy.controller.vo.OfferingVo;
import com.kodomo.yummy.dao.OfferingDao;
import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.entity.order.Order;
import com.kodomo.yummy.entity.order.OrderDetail;
import com.kodomo.yummy.entity.restaurant.Offering;
import com.kodomo.yummy.entity.restaurant.OfferingType;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-21 18:46
 */
@Component
public class OfferingHelper {

    private final OfferingDao offeringDao;
    private final RestaurantDao restaurantDao;

    @Autowired
    public OfferingHelper(OfferingDao offeringDao, RestaurantDao restaurantDao) {
        this.offeringDao = offeringDao;
        this.restaurantDao = restaurantDao;
    }

    /**
     * 创建新的Offering实体, 并检查合法性
     *
     * @param vo
     * @param restaurant
     * @return
     */
    private Offering getNewOfferingForDatabase(OfferingVo vo, Restaurant restaurant) throws ParamErrorException, DateSettingException {
        //检查合法性
        checkValidity(vo, restaurant);

        Offering offering = new Offering();
        offering.setName(vo.getName());
        offering.setPrice(vo.getPrice());
        offering.setNote(vo.getNote());
        offering.setRestaurant(restaurant);
        //设置餐品类型
        Set<OfferingType> types = new HashSet<>();
        vo.getTypes().forEach(t -> types.add(restaurant.getOfferingTypeById(t)));
        offering.setOfferingTypes(types);
        //起止时间
        if (vo.getStartTime() != null) {
            offering.setStartTime(vo.getStartTime());
        } else {
            offering.setStartTime(new Date());
        }
        offering.setEndTime(vo.getEndTime());
        if (vo.getRemaining() != null) {
            offering.setRemainingNumber(vo.getRemaining());
        }
        return offering;
    }

    private void checkValidity(OfferingVo vo, Restaurant restaurant) throws ParamErrorException, DateSettingException {
        if (vo == null || restaurant == null) {
            throw new ParamErrorException();
        }

        List<String> errorFields = new ArrayList<>();
        if (vo.getName() == null || vo.getName().equals("")) {
            errorFields.add("名称");
        }
        if (vo.getPrice() == null || vo.getPrice() < 0) {
            errorFields.add("价格");
        }
        if (vo.getTypes() == null || vo.getTypes().size() != new HashSet<>(vo.getTypes()).size()) {
            errorFields.add("餐品类型");
        }
        if (!errorFields.isEmpty()) {
            //抛出异常
            throw new ParamErrorException(errorFields);
        }
        if (vo.getStartTime() != null && vo.getEndTime() != null && !vo.getStartTime().before(vo.getEndTime())) {
            throw new DateSettingException();
        }
    }

    /**
     * 删除商品信息
     *
     * @param rid
     * @param offeringId
     * @throws ParamErrorException
     * @throws NoSuchAttributeException
     * @throws UnupdatableException
     */
    void deleteOffering(Integer rid, Integer offeringId) throws ParamErrorException, NoSuchAttributeException, UnupdatableException {
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
     * 保存餐品信息
     *
     * @param rid
     * @param vo
     */
    void saveOffering(Integer rid, OfferingVo vo) throws ParamErrorException, UserNotExistsException, UnupdatableException, DateSettingException {
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

        Offering newOffering = getNewOfferingForDatabase(vo, restaurant);
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

    public void remainingReduce(Order order) throws ExceedRemainException {
        List<Offering> offerings = new ArrayList<>();

        List<String> exceedOfferingNames = new ArrayList<>();

        for (OrderDetail detail : order.getDetails()) {
            Offering offering = offeringDao.find(detail.getOfferingId());
            if (offering.getRemainingNumber() == null) {
                continue;//库存不限量, 不进行减少操作
            }
            if (offering.getRemainingNumber() - detail.getQuantity() < 0) {
                exceedOfferingNames.add(offering.getName());
            } else {
                offering.setRemainingNumber(offering.getRemainingNumber() - detail.getQuantity());
                offerings.add(offering);
            }
        }

        if (!exceedOfferingNames.isEmpty()) {
            throw new ExceedRemainException(exceedOfferingNames);
        }

        offeringDao.saveAll(offerings);
    }

    /**
     * 当取消订单时增加响应库存
     *
     * @param order
     */
    public void remainingIncreaseWhenCancelOrder(Order order) {
        List<Offering> offerings = new ArrayList<>();
        for (OrderDetail detail : order.getDetails()) {
            Offering offering = offeringDao.find(detail.getOfferingId());
            if (offering.getRemainingNumber() == null) {
                continue;//库存不限量, 不进行操作
            }

            offering.setRemainingNumber(offering.getRemainingNumber() + detail.getQuantity());
            offerings.add(offering);
        }
        offeringDao.saveAll(offerings);
    }
}
