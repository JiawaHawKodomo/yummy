package com.kodomo.yummy.entity.restaurant;

import com.kodomo.yummy.entity.restaurant.Restaurant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 商家满减
 *
 * @author Shuaiyu Yao
 * @create 2019-02-13 15:36
 */
@Getter
@Setter
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

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public boolean isValidNow() {
        return isValidAt(new Date());
    }

    public boolean isValidAt(Date date) {
        return startDate != null && !startDate.after(date) && (endDate == null || date.before(endDate));
    }

    /**
     * 文字信息,满xx减xx
     *
     * @return
     */
    public String getText() {
        if (getGreaterThan() == null || getDiscount() == null) {
            return "";
        }
        return "满" + getGreaterThan() + "减" + getDiscount();
    }

    public Integer getRestaurantId() {
        if (getRestaurant() == null) return null;
        return getRestaurant().getRestaurantId();
    }

    @Override
    public String toString() {
        return "RestaurantStrategy{" +
                "strategyId=" + strategyId +
                ", greaterThan=" + greaterThan +
                ", discount=" + discount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", restaurant=" + restaurant.getRestaurantId() +
                '}';
    }
}
