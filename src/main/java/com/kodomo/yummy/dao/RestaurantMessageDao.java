package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.restaurant.RestaurantMessage;
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
 * @create 2019-03-02 13:42
 */
@Repository
public interface RestaurantMessageDao extends JpaRepository<RestaurantMessage, Integer>, JpaSpecificationExecutor<RestaurantMessage> {

    default List<RestaurantMessage> getUnreadMessage(Integer rid) {
        return findAll(new Specification<RestaurantMessage>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<RestaurantMessage> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("restaurant").get("restaurantId"), rid),
                        criteriaBuilder.isFalse(root.get("hasBeenRead").as(Boolean.class))
                );
            }
        });
    }
}
