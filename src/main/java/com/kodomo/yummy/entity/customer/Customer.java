package com.kodomo.yummy.entity.customer;

import com.kodomo.yummy.entity.Location;
import com.kodomo.yummy.entity.order.Order;
import com.kodomo.yummy.entity.entity_enum.OrderState;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.exceptions.LackOfBalanceException;
import com.kodomo.yummy.exceptions.ParamErrorException;
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
    @Column(nullable = false, columnDefinition = "integer default 0", insertable = false)
    private Integer level;//等级
    @Column(nullable = false, columnDefinition = "timestamp default now()", insertable = false)
    private Date registerTime;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "_relationship_customer_to_location",
            joinColumns = {@JoinColumn(name = "customer_email")},
            inverseJoinColumns = {@JoinColumn(name = "location_id")})
    private List<Location> locations;//多对多单向

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.DETACH}, mappedBy = "customer")
    private Set<Order> orders;//一对多双向

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.DETACH}, mappedBy = "customer")
    private Set<CustomerRechargeLog> rechargeLogs;

    /**
     * 添加location
     *
     * @param location location
     */
    public void addLocation(Location location) {
        if (getLocations() == null) {
            setLocations(new ArrayList<>());
            getLocations().add(location);
        }
        getLocations().add(location);
    }

    /**
     * 获得还在使用的生效的地址
     *
     * @return set
     */
    
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
        return getLocationById(locationId) != null;
    }

    /**
     * 根据locationId查找有效地址
     *
     * @param locationId
     * @return
     */
    public Location getLocationById(Integer locationId) {
        if (locationId == null) return null;
        for (Location location : getValidLocation()) {
            if (locationId.equals(location.getLocationId())) {
                return location;
            }
        }
        return null;
    }

    /**
     * 获得未支付的订单, 按时间倒序
     *
     * @return set
     */
    
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
    
    public List<Order> getIdleOrders() {
        if (getOrders() == null) return new ArrayList<>();
        return getOrders().stream().filter(o -> o.getState() == OrderState.CANCELLED || o.getState() == OrderState.DONE)
                .sorted(Comparator.comparing(Order::getTheLastUpdateTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 获得完成的订单, 按时间倒序
     *
     * @return
     */
    
    public List<Order> getDoneOrders() {
        if (getOrders() == null) return new ArrayList<>();
        return getOrders().stream()
                .filter(o -> o.getState() == OrderState.DONE)
                .sorted(Comparator.comparing(Order::getTheLastUpdateTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 计算总消费金额
     *
     * @return
     */
    
    public Double getTotalConsumptionAmount() {
        return getDoneOrders().stream()
                .mapToDouble(Order::getTotalPriceAfterDiscount)
                .sum();
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

    /**
     * 增加余额
     *
     * @param amount
     */
    public void increaceBalance(Double amount) throws ParamErrorException {
        if (amount == null || amount <= 0) {
            throw new ParamErrorException("金额");
        }

        if (getBalance() == null) setBalance(amount);
        setBalance(getBalance() + amount);
    }

    /**
     * 减少余额
     *
     * @param amount
     */
    public void reduceBalance(Double amount) throws ParamErrorException, LackOfBalanceException {
        if (amount == null || amount <= 0) {
            throw new ParamErrorException("金额");
        }

        if (getBalance() == null || getBalance() < amount) {
            throw new LackOfBalanceException(getBalance());
        }

        setBalance(getBalance() - amount);
    }

    /**
     * 判断状态是否可用
     *
     * @return
     */
    public boolean isEnable() {
        return getState() == UserState.ACTIVATED;
    }

    /**
     * 按时间倒序, 充值记录
     *
     * @return
     */
    
    public List<CustomerRechargeLog> getRechargeLogsByTimeDesc() {
        if (getRechargeLogs() == null) return new ArrayList<>();
        return getRechargeLogs().stream()
                .sorted((a, b) -> (int) (b.getTime().getTime() - a.getTime().getTime()))
                .collect(Collectors.toList());
    }
}
