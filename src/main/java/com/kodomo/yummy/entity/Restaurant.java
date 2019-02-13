package com.kodomo.yummy.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-13 13:27
 */
@Data
@Entity
@Table(name = "restaurant_info")
public class Restaurant {

    @Id
    private String restaurantId;
    private String address;
    private String telephone;
    @Column(length = 11)
    private String businessHours;//hh:mm-hh:mm
    private String note;//商家备注

    @ManyToMany(mappedBy = "r", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RestaurantType> types;//多对多双向

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Offering> offerings;//一对多双向

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Set<RestaurantStrategy> strategies;//一对多单向
}
