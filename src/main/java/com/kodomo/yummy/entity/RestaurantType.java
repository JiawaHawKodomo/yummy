package com.kodomo.yummy.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-13 13:43
 */
@Entity
@Table(name = "restaurant_type")
@Data
public class RestaurantType {

    @Id
    @GeneratedValue(generator = "generator_rty")
    @GenericGenerator(name = "generator_rty", strategy = "native")
    private Integer typeId;
    @Column(unique = true)
    private String content;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "_relationship_restaurant_to_type",
            joinColumns = {@JoinColumn(name = "restaurant_id")},
            inverseJoinColumns = {@JoinColumn(name = "type_id")})
    private Set<Restaurant> r;

    public boolean isSameWith(String s) {
        return content != null && s != null && content.equals(s);
    }
}
