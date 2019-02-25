package com.kodomo.yummy.controller.vo;

import lombok.Data;

import java.util.Objects;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-25 13:27
 */
@Data
public class OrderRefundStrategyVo {

    private Integer more;
    private Integer less;
    private Double rate;

    public boolean isTimeOverlapping(OrderRefundStrategyVo vo) {
        if (more == null || less == null || vo.more == null || vo.less == null) return false;
        return Objects.equals(more, vo.more) || Objects.equals(less, vo.less) ||
                (more < vo.more && less >= vo.more) || (more > vo.more && more <= vo.less);
    }
}
