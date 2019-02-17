package com.kodomo.yummy.entity;

/**
 * 顾客和店家的账号状态
 *
 * @author Shuaiyu Yao
 * @create 2019-02-13 12:53
 */
public enum UserState {

    UNACTIVATED("未激活"), ACTIVATED("已激活"), UNSUBSCRIBED("已注销");

    private final String text;

    UserState(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
