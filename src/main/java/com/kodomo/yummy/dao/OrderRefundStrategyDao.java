package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.OrderRefundStrategy;
import com.kodomo.yummy.entity.OrderSettlementStrategy;
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
 * @create 2019-02-25 12:55
 */
@Repository
public interface OrderRefundStrategyDao extends JpaRepository<OrderRefundStrategy, Integer>, JpaSpecificationExecutor<OrderRefundStrategy> {

    default OrderRefundStrategy getCurrentStrategy() {
        try {
            Date date = new Date();
            return findOne(new Specification<OrderRefundStrategy>() {
                @Nullable
                @Override
                public Predicate toPredicate(Root<OrderRefundStrategy> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
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
