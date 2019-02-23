package com.kodomo.yummy.entity;

import com.kodomo.yummy.entity.entity_enum.UserState;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    @Column(nullable = false, columnDefinition = "double default 0", insertable = false)
    private Double balance;

    @ManyToMany(targetEntity = RestaurantType.class, fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinTable(name = "_relationship_restaurant_to_type",
            joinColumns = {@JoinColumn(name = "restaurant_id")},
            inverseJoinColumns = {@JoinColumn(name = "type_id")})
    private Set<RestaurantType> types;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Set<OfferingType> offeringTypes;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "restaurant")
    private Set<Offering> offerings;

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "restaurant")
    private Set<RestaurantStrategy> strategies;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @OneToMany(mappedBy = "restaurant", cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Set<Order> orders;

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
        if (getTypes() == null) return "-";
        return new ArrayList<>(types).stream().map(RestaurantType::getContent)
                .reduce((a, b) -> a + "/" + b).orElse("-");
    }

    @NotNull
    public String getLocationInfo() {
        if (getLocation() == null) return "";
        return getLocation().getInfo();
    }

    public String getCity() {
        if (getLocation() == null) return null;
        return getLocation().getCity();
    }

    public String getBlockInfo() {
        if (getLocation() == null) return null;
        return getLocation().getBlockInfo();
    }

    public String getPointInfo() {
        if (getLocation() == null) return null;
        return getLocation().getPointInfo();
    }

    public String getLocationNote() {
        if (getLocation() == null) return null;
        return getLocation().getNote();
    }

    public Double getLat() {
        if (getLocation() == null) return null;
        return getLocation().getLat();
    }

    public Double getLng() {
        if (getLocation() == null) return null;
        return getLocation().getLng();
    }

    public double getOrderQuantity() {
        if (getOrders() == null) return 0;
        return getOrders().size();
    }

    /**
     * 按金额获取有效的strategy
     *
     * @return
     */
    @NotNull
    public List<RestaurantStrategy> getRestaurantValidStrategyByAmount() {
        if (getStrategies() == null) return new ArrayList<>();
        return getStrategies().stream().filter(RestaurantStrategy::isValidNow)
                .sorted(Comparator.comparingDouble(RestaurantStrategy::getDiscount))
                .collect(Collectors.toList());
    }

    /**
     * 获取有效商品
     *
     * @return
     */
    @NotNull
    public List<Offering> getValidOffering() {
        if (getOfferings() == null) return new ArrayList<>();
        return getOfferings().stream().filter(Offering::isOnSale)
                .collect(Collectors.toList());
    }

    /**
     * 根据名字查找查询
     *
     * @return
     */
    public OfferingType getOfferingTypeById(Integer id) {
        if (getOfferingTypes() == null || id == null) return null;
        for (OfferingType type : getOfferingTypes()) {
            if (id.equals(type.getOfferingTypeId())) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据顺序取商品类型列表
     *
     * @return
     */
    @NotNull
    public List<OfferingType> getOfferingTypeByOrder() {
        if (getOfferingTypes() == null) return new ArrayList<>();
        return getOfferingTypes().stream().sorted(Comparator.comparingInt(OfferingType::getSequenceNumber))
                .collect(Collectors.toList());
    }

    /**
     * 获取无类别餐品
     *
     * @return
     */
    @NotNull
    public List<Offering> getNontypeOfferings() {
        if (getOfferings() == null) return new ArrayList<>();
        return getOfferings().stream().filter(o -> o.getOfferingTypes().size() == 0)
                .collect(Collectors.toList());
    }

    /**
     * 根据id查找strategy
     *
     * @param strategyId
     * @return
     */
    public RestaurantStrategy getRestaurantStrategyById(Integer strategyId) {
        if (getStrategies() == null || strategyId == null) return null;
        for (RestaurantStrategy strategy : getStrategies()) {
            if (strategyId.equals(strategy.getStrategyId())) {
                return strategy;
            }
        }
        return null;
    }

    /**
     * 餐厅满减策略的文字说明
     *
     * @return
     */
    @NotNull
    public String getRestaurantStrategyText() {
        if (getRestaurantValidStrategyByAmount() == null) return "无";
        return getRestaurantValidStrategyByAmount().stream().map(RestaurantStrategy::getText)
                .reduce((a, b) -> a + "," + b).orElse("无");
    }

    /**
     * 判断餐厅是否匹配关键字
     *
     * @param keyWord
     * @return 匹配关键字的信息, 如果完全不匹配则返回null
     */
    public String isMatched(String keyWord) {
        if (keyWord == null) return null;

        //餐厅名称
        List<String> matchInfo = new ArrayList<>();
        if (getName() != null && getName().contains(keyWord)) {
            matchInfo.add("餐厅名称:" + getName());
        }

        //菜品名称
        if (getValidOffering() != null) {
            getValidOffering().forEach(offering -> {
                if (offering.getName() != null && offering.getName().contains(keyWord)) {
                    matchInfo.add("菜品:" + offering.getName());
                }
            });
        }

        return matchInfo.stream().reduce((a, b) -> a + "," + b).orElse(null);
    }

    /**
     * 计算获得适配的最佳餐厅满减策略
     *
     * @param money
     * @return 均不满足则返回null
     */
    public RestaurantStrategy getAppliedRestaurantStrategy(Double money) {
        if (money == null || getRestaurantValidStrategyByAmount() == null) return null;
        double discount = 0;
        RestaurantStrategy result = null;
        for (RestaurantStrategy strategy : getRestaurantValidStrategyByAmount()) {
            if (strategy.getGreaterThan() == null || strategy.getDiscount() == null) continue;
            if (strategy.getGreaterThan() >= money && discount < strategy.getDiscount()) {
                discount = strategy.getDiscount();
                result = strategy;
            }
        }
        return result;
    }
}
