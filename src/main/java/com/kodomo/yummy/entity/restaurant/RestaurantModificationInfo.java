package com.kodomo.yummy.entity.restaurant;

import com.kodomo.yummy.entity.entity_enum.RestaurantModificationState;
import com.kodomo.yummy.entity.util.restaurant.RestaurantTypeHelper;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-28 17:08
 */
@Entity
@Getter
@Setter
@Table(name = "restaurant_modification_info")
public class RestaurantModificationInfo {

    @Id
    @Column(name = "restaurant_modification_info_id")
    @GeneratedValue(generator = "generator_rmi")
    @GenericGenerator(name = "generator_rmi", strategy = "native")
    private Integer restaurantModificationInfoId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "state", nullable = false, insertable = false, columnDefinition = "integer default 0")
    private RestaurantModificationState state;

    @Column(name = "submit_time", nullable = false, insertable = false, columnDefinition = "timestamp default now()")
    private Date submitTime;

    @Column(name = "check_time")
    private Date checkTime;//审核时间

    @Column(nullable = false)
    private String name;//名字
    @Column(nullable = false)
    private String telephone;//电话
    @Column(name = "run_from", nullable = false)
    private Time runFrom;
    @Column(name = "run_to", nullable = false)
    private Time runTo;

    @ManyToMany(targetEntity = RestaurantType.class, fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.DETACH})
    @JoinTable(name = "_relationship_restaurant_modification_to_type",
            joinColumns = {@JoinColumn(name = "restaurant_modification_info_id")},
            inverseJoinColumns = {@JoinColumn(name = "type_id")})
    private Set<RestaurantType> types;
    @Column(name = "location_note")
    private String locationNote;

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

    public String getTypeByString() {
        return RestaurantTypeHelper.typesToString(getTypes());
    }
}
