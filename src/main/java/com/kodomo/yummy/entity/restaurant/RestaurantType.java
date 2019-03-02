package com.kodomo.yummy.entity.restaurant;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-13 13:43
 */
@Entity
@Table(name = "restaurant_type")
@Getter
@Setter
public class RestaurantType {

    @Id
    @GeneratedValue(generator = "generator_rty")
    @GenericGenerator(name = "generator_rty", strategy = "native")
    private Integer typeId;
    @Column(unique = true)
    private String content;

    @ManyToMany(mappedBy = "types", fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    private Set<Restaurant> restaurants;

    public boolean isSameWith(String s) {
        return content != null && s != null && content.equals(s);
    }

    @Override
    public String toString() {
        return "RestaurantType{" +
                "typeId=" + typeId +
                ", content='" + content + '\'' +
                '}';
    }
}
