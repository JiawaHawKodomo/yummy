package com.kodomo.yummy.entity;

import com.kodomo.yummy.entity.entity_enum.OrderState;
import com.kodomo.yummy.entity.entity_enum.UserState;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-13 12:04
 */
@Entity
@Table(name = "customer_info")
@Data
public class Customer {

    @Id
    private String email;
    private String password;
    private String name;
    private String telephone;
    @Column(name = "state", nullable = false, columnDefinition = "integer default 0")
    private UserState state;
    @Column(nullable = false, columnDefinition = "double default 0", insertable = false)
    private Double balance;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "_relationship_customer_to_location",
            joinColumns = {@JoinColumn(name = "customer_email")},
            inverseJoinColumns = {@JoinColumn(name = "location_id")})
    private Set<Location> locations;//多对多单向

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.DETACH}, mappedBy = "customer")
    private Set<Order> orders;//一对多双向

    /**
     * 添加location
     *
     * @param location location
     */
    public void addLocation(Location location) {
        if (getLocations() == null) {
            setLocations(new HashSet<>());
            getLocations().add(location);
        }
        getLocations().add(location);
    }

    /**
     * 获得还在使用的生效的地址
     *
     * @return set
     */
    @NotNull
    public Set<Location> getValidLocation() {
        if (getLocations() == null) return new HashSet<>();
        return getLocations().stream().filter(Location::getIsInUse).collect(Collectors.toSet());
    }

    /**
     * 判断是否拥有该位置
     *
     * @param locationId locationid
     * @return boolean
     */
    public boolean hasLocation(Integer locationId) {
        if (locationId == null) return false;
        return getLocations().stream().map(l -> l.getLocationId().equals(locationId))
                .reduce((a, b) -> a || b).orElse(false);
    }

    /**
     * 获得未支付的订单, 按时间倒序
     *
     * @return set
     */
    @NotNull
    public List<Order> getUnpaidOrders() {
        if (getOrders() == null) return new ArrayList<>();
        return getOrders().stream().filter(o -> o.getState() == OrderState.UNPAID)
                .sorted(Comparator.comparing(Order::getTheLastUpdateTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 获得进行中的订单, 按时间倒序
     *
     * @return
     */
    @NotNull
    public List<Order> getOngoingOrders() {
        if (getOrders() == null) return new ArrayList<>();
        return getOrders().stream().filter(o -> o.getState() == OrderState.ONGOING)
                .sorted(Comparator.comparing(Order::getTheLastUpdateTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 获得历史订单, 按时间倒序
     *
     * @return
     */
    @NotNull
    public List<Order> getIdleOrders() {
        if (getOrders() == null) return new ArrayList<>();
        return getOrders().stream().filter(o -> o.getState() == OrderState.CANCELLED || o.getState() == OrderState.DONE)
                .sorted(Comparator.comparing(Order::getTheLastUpdateTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 根据id获取order实体
     *
     * @param id
     * @return
     */
    public Order getOrderByOrderId(Integer id) {
        if (id == null) return null;
        for (Order order : getOrders()) {
            if (id.equals(order.getOrderId()))
                return order;
        }
        return null;
    }
}
