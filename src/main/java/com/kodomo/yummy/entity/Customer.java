package com.kodomo.yummy.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
    private CustomerState state;
}
