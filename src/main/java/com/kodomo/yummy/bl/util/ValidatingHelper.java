package com.kodomo.yummy.bl.util;

import org.springframework.stereotype.Component;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-17 20:37
 */
@Component
public class ValidatingHelper {

    public boolean isEmail(String s) {
        return s != null && s.matches("\\w+[\\w]*@[\\w]+\\.[\\w]+$");
    }

    public boolean isTelephone(String s) {
        return s != null && s.matches("1[0-9]{10}");
    }
}
