package com.kodomo.yummy.entity.order;

import com.kodomo.yummy.entity.entity_enum.OrderState;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 记录订单状态转换的日志
 *
 * @author Shuaiyu Yao
 * @create 2019-02-20 15:52
 */
@Getter
@Setter
@Entity
@Table(name = "order_log")
public class OrderLog {

    @Id
    @Column(name = "order_log_id")
    @GeneratedValue(generator = "generator_odl")
    @GenericGenerator(name = "generator_odl", strategy = "native")
    private Integer orderLogId;

    @Column(updatable = false, insertable = false, nullable = false, columnDefinition = "timestamp default now()")
    private Date date;//记录时间

    @Column(name = "to_state", updatable = false, nullable = false)
    private OrderState toState;//转换为的状态

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "order_id")
    private Order order;

    @Override
    public String toString() {
        return "OrderLog{" +
                "orderLogId=" + orderLogId +
                ", date=" + date +
                ", toState=" + toState +
                ", order=" + order.getOrderId() +
                '}';
    }
}
