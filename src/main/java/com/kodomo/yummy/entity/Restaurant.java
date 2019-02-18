package com.kodomo.yummy.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    @Column(name = "register_time", updatable = false, columnDefinition = "timestamp default current_timestamp()")
    private Date registerTime;

    @ManyToMany(targetEntity = RestaurantType.class, fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinTable(name = "_relationship_restaurant_to_type",
            joinColumns = {@JoinColumn(name = "restaurant_id")},
            inverseJoinColumns = {@JoinColumn(name = "type_id")})
    private Set<RestaurantType> types;//多对多双向

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Set<OfferingType> offeringTypes;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Set<Offering> offerings;//一对多双向

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Set<RestaurantStrategy> strategies;//一对多单向

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;//多对一单向

    /**
     * 用String表示营业时间
     *
     * @return
     */
    public String getBusinessHours() {
        if (runFrom == null || runTo == null) return "-";
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(runFrom) + "-" + format.format(runTo);
    }

    /**
     * 用String表示餐厅类型
     *
     * @return
     */
    public String getTypeByString() {
        if (types == null) return "-";
        return new ArrayList<>(types).stream().map(RestaurantType::getContent)
                .reduce((a, b) -> a + "/" + b).orElse("-");
    }

    public String getLocationInfo() {
        if (location == null) return null;
        return location.getInfo();
    }

    public String getCity() {
        if (location == null) return null;
        return location.getCity();
    }

    public String getBlockInfo() {
        if (location == null) return null;
        return location.getBlockInfo();
    }

    public String getPointInfo() {
        if (location == null) return null;
        return location.getPointInfo();
    }

    public String getLocationNote() {
        if (location == null) return null;
        return location.getNote();
    }

    public Double getLat() {
        if (location == null) return null;
        return location.getLat();
    }

    public Double getLng() {
        if (location == null) return null;
        return location.getLng();
    }
}
