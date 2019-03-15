package com.kodomo.yummy.entity.customer;

import com.kodomo.yummy.entity.Manager;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 会员等级策略
 *
 * @author Shuaiyu Yao
 * @create 2019-02-27 10:18
 */
@Entity
@Getter
@Setter
@Table(name = "customer_level_strategy")
public class CustomerLevelStrategy {

    @Id
    @Column(name = "customer_level_strategy_id")
    @GeneratedValue(generator = "generator_cls")
    @GenericGenerator(name = "generator_cls", strategy = "native")
    private Integer customerLevelStrategyId;

    @Column(name = "start_time", columnDefinition = "timestamp default now()")
    private Date startTime;
    @Column(name = "end_time", columnDefinition = "timestamp default '2037-01-01'", insertable = false)
    private Date endTime;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Manager manager;//制定人

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_level_strategy_id")
    private Set<CustomerLevelStrategyDetail> details;

    
    public List<CustomerLevelStrategyDetail> getDetailsByOrder() {
        if (getDetails() == null) return new ArrayList<>();
        return getDetails().stream()
                .sorted(Comparator.comparingInt(CustomerLevelStrategyDetail::getLevel))
                .collect(Collectors.toList());
    }

    
    public Integer calculateLevel(Double totalConsumptionAmount) {
        int result = 0;
        for (CustomerLevelStrategyDetail detail : getDetailsByOrder()) {
            if (detail.getConsumptionAmount() > totalConsumptionAmount) {
                return result;
            } else {
                result = detail.getLevel();
            }
        }
        return result;
    }

    
    public double getDiscountRate(Customer customer) {
        Integer level = customer.getLevel() == null ? 0 : customer.getLevel();
        if (level == 0) return 0;
        for (CustomerLevelStrategyDetail detail : getDetails()) {
            if (Objects.equals(level, detail.getLevel())) {
                return detail.getDiscountRate();
            }
        }
        return 0;
    }
}
