package com.kodomo.yummy.entity.util.restaurant;

import com.kodomo.yummy.entity.RestaurantType;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Shuaiyu Yao
 * @create 2019-03-01 17:07
 */
public class RestaurantTypeHelper {

    @NotNull
    public static String typesToString(Collection<RestaurantType> types) {
        if (types == null) return "-";
        return new ArrayList<>(types).stream().map(RestaurantType::getContent)
                .sorted()
                .reduce((a, b) -> a + "/" + b).orElse("-");
    }
}
