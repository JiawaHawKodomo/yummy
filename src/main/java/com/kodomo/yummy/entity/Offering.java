package com.kodomo.yummy.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-13 14:29
 */
@Entity
@Data
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
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


    /**
     * 是否正在销售
     *
     * @return boolean
     */
    public boolean isOnSale() {
        return endTime == null || endTime.after(new Date());
    }
}
