package com.kodomo.yummy.entity.restaurant;

import com.kodomo.yummy.entity.*;
import com.kodomo.yummy.entity.entity_enum.RestaurantModificationState;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.entity.order.Order;
import com.kodomo.yummy.entity.util.restaurant.RestaurantTypeHelper;
import com.kodomo.yummy.exceptions.LackOfBalanceException;
import com.kodomo.yummy.exceptions.ParamErrorException;
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

    @ManyToMany(targetEntity = RestaurantType.class, fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "_relationship_restaurant_to_type",
            joinColumns = {@JoinColumn(name = "restaurant_id")},
            inverseJoinColumns = {@JoinColumn(name = "type_id")})
    private List<RestaurantType> types;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Set<OfferingType> offeringTypes;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "restaurant")
    private List<Offering> offerings;

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "restaurant")
    private List<RestaurantStrategy> strategies;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @OneToMany(mappedBy = "restaurant", cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Set<Order> orders;

    @OneToMany(mappedBy = "restaurant", cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Set<RestaurantModificationInfo> modificationInfos;

    @OneToMany(mappedBy = "restaurant", cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
    private List<RestaurantMessage> messages;

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
     * 判断是否正在营业
     *
     * @return
     */
    public boolean isOpenNow() {
        if (getRunFrom() == null || getRunTo() == null) {
            return false;
        }

        int from = dateToMinute(getRunFrom());
        int to = dateToMinute(getRunTo());
        int now = dateToMinute(new Date());
        return now >= from && now <= to;
    }

    private int dateToMinute(Date date) {
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
        return Integer.parseInt(hourFormat.format(date)) * 60 + Integer.parseInt(minuteFormat.format(date));
    }

    public int getUnreadMessageQuantity() {
        if (getMessages() == null) return 0;
        return (int) getMessages().stream()
                .filter(restaurantMessage -> !restaurantMessage.getHasBeenRead())
                .count();
    }


    public List<RestaurantMessage> getMessagesByTimeOrderDesc() {
        if (getMessages() == null) return new ArrayList<>();
        return getMessages().stream()
                .sorted((a, b) -> (int) (b.getTime().getTime() - a.getTime().getTime()))
                .collect(Collectors.toList());
    }

    /**
     * 用String表示餐厅类型
     *
     * @return
     */
    public String getTypeByString() {
        return RestaurantTypeHelper.typesToString(getTypes());
    }


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

    public void setLocationNote(String locationNote) {
        if (getLocation() == null) return;
        getLocation().setNote(locationNote);
    }

    /**
     * 按金额获取有效的strategy
     *
     * @return
     */

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

    public List<Offering> getValidOffering() {
        if (getOfferings() == null) return new ArrayList<>();
        return getOfferings().stream().filter(Offering::isValid)
                .collect(Collectors.toList());
    }

    /**
     * 获取在售商品
     *
     * @return
     */

    public List<Offering> getOnSaleOffering() {
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
        if (getOnSaleOffering() == null || id == null) return null;
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

    public List<OfferingType> getOfferingTypeByOrder() {
        if (getOnSaleOffering() == null) return new ArrayList<>();
        return getOfferingTypes().stream().sorted(Comparator.comparingInt(OfferingType::getSequenceNumber))
                .collect(Collectors.toList());
    }

    /**
     * 获取无类别餐品
     *
     * @return
     */

    public List<Offering> getNontypeOfferings() {
        if (getOnSaleOffering() == null) return new ArrayList<>();
        return getOnSaleOffering().stream().filter(o -> o.getOfferingTypes().size() == 0)
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
        if (keyWord.equals("")) return "";

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
            if (strategy.getGreaterThan() <= money && discount < strategy.getDiscount()) {
                discount = strategy.getDiscount();
                result = strategy;
            }
        }
        return result;
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


    public List<Order> getOngoingOrders() {
        if (getOrders() == null) return new ArrayList<>();
        return getOrders().stream().filter(Order::isOngoing)
                .sorted((a, b) -> (int) (b.getCreateTime().getTime() - a.getCreateTime().getTime()))
                .collect(Collectors.toList());
    }


    public List<Order> getUnpaidOrders() {
        if (getOrders() == null) return new ArrayList<>();
        return getOrders().stream().filter(Order::isUnpaid)
                .sorted((a, b) -> (int) (b.getCreateTime().getTime() - a.getCreateTime().getTime()))
                .collect(Collectors.toList());
    }


    public List<Order> getIdleOrders() {
        if (getOrders() == null) return new ArrayList<>();
        return getOrders().stream().filter(o -> !o.isOngoing() && !o.isUnpaid())
                .sorted((a, b) -> (int) (b.getCreateTime().getTime() - a.getCreateTime().getTime()))
                .collect(Collectors.toList());
    }


    public List<Order> getCanceledOrders() {
        if (getOrders() == null) return new ArrayList<>();
        return getOrders().stream().filter(Order::isCanceled)
                .sorted((a, b) -> (int) (b.getCreateTime().getTime() - a.getCreateTime().getTime()))
                .collect(Collectors.toList());
    }


    public List<Order> getDoneOrders() {
        if (getOrders() == null) return new ArrayList<>();
        return getOrders().stream().filter(Order::isDone)
                .sorted((a, b) -> (int) (b.getCreateTime().getTime() - a.getCreateTime().getTime()))
                .collect(Collectors.toList());
    }

    public Order getOrderById(Integer orderId) {
        if (getOrders() == null || orderId == null) return null;
        for (Order order : getOrders()) {
            if (orderId.equals(order.getOrderId())) {
                return order;
            }
        }
        return null;
    }

    public String getStateText() {
        if (getState() == null) return "-";
        return getState().getText();
    }

    public RestaurantModificationInfo getWaitingConfirmModificationInfo() {
        if (getModificationInfos() == null || getModificationInfos().isEmpty()) return null;
        List<RestaurantModificationInfo> infos = getModificationInfos().stream()
                .filter(restaurantModificationInfo -> restaurantModificationInfo.getState() == RestaurantModificationState.WAIT_CONFIRM)
                .collect(Collectors.toList());

        if (infos.isEmpty()) {
            return null;
        } else {
            return infos.get(0);
        }
    }
}
