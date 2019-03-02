package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.restaurant.RestaurantType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-17 23:43
 */
@Repository
public interface RestaurantTypeDao extends JpaRepository<RestaurantType, Integer>, JpaSpecificationExecutor<RestaurantType> {

    default RestaurantType findTypeByContent(String content) {
        try {
            return findOne(new Specification<RestaurantType>() {
                @Nullable
                @Override
                public Predicate toPredicate(Root<RestaurantType> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    return criteriaBuilder.equal(root.get("content").as(String.class), content);
                }
            }).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
