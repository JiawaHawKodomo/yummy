package com.kodomo.yummy.entity.restaurant;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商家的餐品分类
 *
 * @author Shuaiyu Yao
 * @create 2019-02-16 10:46
 */
@Data
@Entity
@Table(name = "offering_type",
        indexes = {@Index(name = "name_restaurant", columnList = "restaurant_id,name", unique = true)})
public class OfferingType {

    @Id
    @Column(name = "offering_type_id")
    @GeneratedValue(generator = "generator_oft")
    @GenericGenerator(name = "generator_oft", strategy = "native")
    private Integer offeringTypeId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, name = "sequence_number")
    private Integer sequenceNumber;
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH}, mappedBy = "offeringTypes")
    private List<Offering> offerings;

    public boolean contains(Offering offering) {
        if (getOfferings() == null || offering == null || offering.getOfferingId() == null) return false;
        for (Offering o : getOfferings()) {
            if (offering.getOfferingId().equals(o.getOfferingId())) {
                return true;
            }
        }
        return false;
    }

    public List<Offering> getOnSaleOfferings() {
        if (getOfferings() == null) return new ArrayList<>();
        return getOfferings().stream().filter(Offering::isOnSale)
                .collect(Collectors.toList());
    }
}
