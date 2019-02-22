package com.kodomo.yummy.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

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
    private Set<Offering> offerings;
}
