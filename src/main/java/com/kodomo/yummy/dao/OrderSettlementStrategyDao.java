package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.order.OrderSettlementStrategy;
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
 * @create 2019-02-19 11:18
 */
@Repository
public interface OrderSettlementStrategyDao extends JpaRepository<OrderSettlementStrategy, Integer>, JpaSpecificationExecutor<OrderSettlementStrategy> {

    /**
     * 获取当前策略
     *
     * @return 当前策略
     */
    default OrderSettlementStrategy getCurrentOrderSettlementStrategy() {
        try {
            Date date = new Date();
            return findOne(new Specification<OrderSettlementStrategy>() {
                @Nullable
                @Override
                public Predicate toPredicate(Root<OrderSettlementStrategy> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
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
