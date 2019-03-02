package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.customer.CustomerLevelStrategy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-27 10:32
 */
@Repository
public interface CustomerLevelStrategyDao extends JpaRepository<CustomerLevelStrategy, Integer>, JpaSpecificationExecutor<CustomerLevelStrategy> {

    default CustomerLevelStrategy getCurrentStrategy() {
        try {
            Date date = new Date();
            return findOne(new Specification<CustomerLevelStrategy>() {
                @Nullable
                @Override
                public Predicate toPredicate(Root<CustomerLevelStrategy> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    return criteriaBuilder.and(
                            criteriaBuilder.lessThanOrEqualTo(root.get("startTime").as(Date.class), date),
                            criteriaBuilder.or(
                                    criteriaBuilder.greaterThanOrEqualTo(root.get("endTime").as(Date.class), date),
                                    criteriaBuilder.isNull(root.get("endTime").as(Date.class))
                            )
                    );
                }
            }).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
