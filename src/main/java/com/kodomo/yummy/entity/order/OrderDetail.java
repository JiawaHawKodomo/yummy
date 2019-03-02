package com.kodomo.yummy.entity.order;

import com.kodomo.yummy.entity.restaurant.Offering;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-13 15:10
 */
@Getter
@Setter
@Entity
@Table(name = "order_detail")
public class OrderDetail {

    @Id
    @GeneratedValue(generator = "generator_odd")
    @GenericGenerator(name = "generator_odd", strategy = "native")
    private Integer id;
    @Column(nullable = false)
    private Integer quantity;//购买数量
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "offering_id", nullable = false)
    private Offering offering;//购买商品

    private double getSinglePrice() {
        if (offering == null || offering.getPrice() == null) return 0;
        return offering.getPrice();
    }

    public double getTotalPrice() {
        if (quantity == null) return 0;
        return getSinglePrice() * quantity;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", offering=" + offering.getName() +
                '}';
    }
}
