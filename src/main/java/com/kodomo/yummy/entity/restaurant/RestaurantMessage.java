package com.kodomo.yummy.entity.restaurant;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 商家信息
 *
 * @author Shuaiyu Yao
 * @create 2019-03-02 13:22
 */
@Entity
@Getter
@Setter
@Table(name = "restaurant_message")
public class RestaurantMessage {

    @Id
    @Column(name = "restaurant_message_id")
    @GeneratedValue(generator = "generator_rms")
    @GenericGenerator(name = "generator_rms", strategy = "native")
    private Integer restaurantMessageId;
    @Column(updatable = false, insertable = false, nullable = false, columnDefinition = "timestamp default now()")
    private Date time;
    @Column(updatable = false, nullable = false)
    private String content;

    @ManyToOne(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "has_been_read", insertable = false, nullable = false, columnDefinition = "boolean default false")
    private Boolean hasBeenRead;//已读
}
