package com.kodomo.yummy.entity;

import com.kodomo.yummy.entity.entity_enum.UserState;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

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

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.DETACH})
    @JoinTable(name = "_relationship_customer_to_location",
            joinColumns = {@JoinColumn(name = "customer_email")},
            inverseJoinColumns = {@JoinColumn(name = "location_id")})
    private Set<Location> locations;//多对多单向
}
