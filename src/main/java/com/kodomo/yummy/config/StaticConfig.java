package com.kodomo.yummy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 静态常量
 *
 * @author Shuaiyu Yao
 * @create 2019-02-13 15:59
 */
@Component
public class StaticConfig {

    @Value("${yummy-system.min-payment}")
    private double minPayment;
    @Value("${yummy-system.max-distribution-distance}")
    private double maxDistributionDistance;

    private static StaticConfig instance;

    @PostConstruct
    public void afterBoot() {
        instance = this;
    }

    public static double getMinPayment() {
        return instance.minPayment;
    }

    public static double getMaxDistributionDistance() {
        return instance.maxDistributionDistance;
    }
}
