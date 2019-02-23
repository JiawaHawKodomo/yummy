package com.kodomo.yummy.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-23 22:28
 */
@Getter
@Setter
@Entity
@Table(name = "customer_recharge_log")
public class CustomerRechargeLog {

    @Id
    @Column(name = "customer_recharge_id")
    @GeneratedValue(generator = "generator_rch")
    @GenericGenerator(name = "generator_rch", strategy = "native")
    private Integer customerRechargeId;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    private Customer customer;
    @Column(updatable = false, nullable = false)
    private Double amount;
    @Column(updatable = false, nullable = false, insertable = false, columnDefinition = "timestamp default now()")
    private Date time;

    @Override
    public String toString() {
        return "CustomerRechargeLog{}";
    }
}
