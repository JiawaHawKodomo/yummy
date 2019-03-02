package com.kodomo.yummy.entity.restaurant;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-13 14:29
 */
@Entity
@Getter
@Setter
@Table(name = "offering_info")
public class Offering {

    @Id
    @GeneratedValue(generator = "generator_ofr")
    @GenericGenerator(name = "generator_ofr", strategy = "native")
    private Integer offeringId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double price;
    private String note;//商家备注

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(name = "start_time", columnDefinition = "timestamp default current_timestamp()", updatable = false)
    private Date startTime;//起售日期

    /**
     * 结束日期, 用于当商家需要修改商品信息时, 数据逻辑层不对原数据进行修改, 而是结束原商品日期, 生成新的商品信息,
     * 如果为空则表示正在出售
     */
    @Column(name = "end_time")
    private Date endTime;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.PERSIST})
    @JoinTable(name = "_relationship_type_to_offering",
            joinColumns = {@JoinColumn(name = "offering_id")},
            inverseJoinColumns = {@JoinColumn(name = "offering_type_id")})
    private Set<OfferingType> offeringTypes;

    /**
     * 是否正在销售或者正在预售
     *
     * @return boolean
     */
    public boolean isValid() {
        return endTime == null || endTime.after(new Date());
    }

    @Override
    public String toString() {
        return "Offering{" +
                "offeringId=" + offeringId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", note='" + note + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public String getOfferingTypeJoinBy(String s) {
        if (offeringTypes == null) return "";
        return offeringTypes.stream().map(t -> t.getOfferingTypeId().toString())
                .reduce((a, b) -> a + s + b).orElse(s);
    }

    /**
     * 判断是否无类别
     *
     * @return
     */
    public boolean isNonTyped() {
        return getOfferingTypes() == null || getOfferingTypes().size() == 0;
    }

    public Integer getRestaurantId() {
        if (getRestaurant() == null) return null;
        return getRestaurant().getRestaurantId();
    }
}


