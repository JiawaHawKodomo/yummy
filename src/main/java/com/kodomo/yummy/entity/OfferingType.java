package com.kodomo.yummy.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

/**
 * 商家的餐品分类
 *
 * @author Shuaiyu Yao
 * @create 2019-02-16 10:46
 */
@Data
@Entity
@Table(name = "offering_type",
        indexes = {@Index(name = "name_restaurant", columnList = "name,restaurant_id", unique = true)})
public class OfferingType {

    @Id
    @Column(name = "offering_type_id")
    private Integer offeringTypeId;
    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.PERSIST})
    @JoinTable(name = "_relationship_type_to_offering",
            joinColumns = {@JoinColumn(name = "offering_type_id")},
            inverseJoinColumns = {@JoinColumn(name = "offering_id")})
    private Set<Offering> offerings;

}
