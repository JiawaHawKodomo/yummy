package com.kodomo.yummy.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-18 10:38
 */
@Data
@Entity
@Table(name = "manager_info")
public class Manager {

    @Id
    private String managerId;
    @Column(nullable = false)
    private String password;
}
