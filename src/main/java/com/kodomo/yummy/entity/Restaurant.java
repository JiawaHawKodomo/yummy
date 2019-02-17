package com.kodomo.yummy.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Time;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_rtr")
    @SequenceGenerator(name = "generator_rtr", sequenceName = "__generator_restaurant", allocationSize = 1, initialValue = 1000000)
    private Integer restaurantId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    @Column(unique = true, nullable = false)
    private String telephone;
    @Column(name = "run_from", nullable = false)
    private Time runFrom;
    @Column(name = "run_to", nullable = false)
    private Time runTo;
    private String note;//商家备注
    @Column(name = "state", columnDefinition = "integer default 0")
    private UserState state;//商家状态

    @ManyToMany(mappedBy = "r", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private Set<RestaurantType> types;//多对多双向

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Set<OfferingType> offeringTypes;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Offering> offerings;//一对多双向

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Set<RestaurantStrategy> strategies;//一对多单向

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;//多对一单向
}
