package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.restaurant.RestaurantModificationInfo;
import com.kodomo.yummy.entity.entity_enum.RestaurantModificationState;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-28 18:19
 */
@Repository
public interface RestaurantModificationInfoDao extends JpaRepository<RestaurantModificationInfo, Integer>, JpaSpecificationExecutor<RestaurantModificationInfo> {

    default RestaurantModificationInfo find(Integer id) {
        if (id == null) return null;
        return findById(id).orElse(null);
    }

    default List<RestaurantModificationInfo> getWaitingModificationInfo() {
        return findAll(new Specification<RestaurantModificationInfo>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<RestaurantModificationInfo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("state").as(RestaurantModificationState.class), RestaurantModificationState.WAIT_CONFIRM);
            }
        });
    }

}
