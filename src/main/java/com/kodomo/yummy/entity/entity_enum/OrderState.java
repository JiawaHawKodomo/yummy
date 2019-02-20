package com.kodomo.yummy.entity.entity_enum;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-20 15:45
 */
public enum OrderState {

    UNPAID("未付款"), ONGOING("配餐中"), DONE("已完成"), CANCELLED("已取消");

    OrderState(String text) {
        this.text = text;
    }

    private String text;

    public String getText() {
        return text;
    }
}
