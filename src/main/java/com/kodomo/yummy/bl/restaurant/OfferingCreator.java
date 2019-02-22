package com.kodomo.yummy.bl.restaurant;

import com.kodomo.yummy.controller.vo.OfferingVo;
import com.kodomo.yummy.entity.Offering;
import com.kodomo.yummy.entity.OfferingType;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.exceptions.ParamErrorException;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-21 18:46
 */
@Component
public class OfferingCreator {

    /**
     * 创建新的Offering实体, 并检查合法性
     *
     * @param vo
     * @param restaurant
     * @return
     */
    public Offering getNewOfferingForDatabase(OfferingVo vo, Restaurant restaurant) throws ParamErrorException {
        //检查合法性
        checkValidity(vo, restaurant);

        Offering offering = new Offering();
        offering.setName(vo.getName());
        offering.setPrice(vo.getPrice());
        offering.setNote(vo.getNote());
        offering.setStartTime(new Date());
        offering.setRestaurant(restaurant);
        //设置餐品类型
        Set<OfferingType> types = new HashSet<>();
        vo.getTypes().forEach(t -> types.add(restaurant.getOfferingTypeById(t)));
        offering.setOfferingTypes(types);
        return offering;
    }

    private void checkValidity(OfferingVo vo, Restaurant restaurant) throws ParamErrorException {
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
    }
}
