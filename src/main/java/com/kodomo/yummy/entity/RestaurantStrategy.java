package com.kodomo.yummy.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 商家满减
 *
 * @author Shuaiyu Yao
 * @create 2019-02-13 15:36
 */
@Data
@Entity
@Table(name = "restaurant_strategy")
public class RestaurantStrategy {

    @Id
    @GeneratedValue(generator = "generator_stg")
    @GenericGenerator(name = "generator_stg", strategy = "native")
    private Integer strategyId;

    @Column(name = "greater_than", nullable = false, updatable = false)
    private Double greaterThan;
    @Column(name = "discount", nullable = false, updatable = false)
    private Double discount;
    @Column(name = "start_date", nullable = false, updatable = false, columnDefinition = "timestamp default current_timestamp()")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;

    public boolean isValidNow() {
        return isValidAt(new Date());
    }

    public boolean isValidAt(Date date) {
        return startDate != null && !startDate.after(date) && (endDate == null || date.before(endDate));
    }
}
