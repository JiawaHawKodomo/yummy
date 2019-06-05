package com.kodomo.yummy.util.testdata;

import com.kodomo.yummy.util.Utility;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-06-04 13:05
 */
@Component
public class RandomHelper {

    private List<String> emailDomainNames = new ArrayList<>(Arrays.asList("qq.com", "163.com", "125.com",
            "google.com", "smail.nju.edu.cn"));

    String randomTelephone() {
        StringBuilder sb = new StringBuilder();
        sb.append('1');
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append((char) ('0' + random.nextInt(10)));
        }
        return sb.toString();
    }

    String randomIndex(int bound) {
        return String.valueOf(randomInt(bound));
    }

    int randomInt(int bound) {
        Random random = new Random();
        return random.nextInt(bound);
    }

    boolean randomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }

    String randomEmail() {
        String prefix = Utility.getSHA256Str(String.valueOf(System.nanoTime())).substring(0, 8);
        return prefix + "@" + emailDomainNames.get(randomInt(emailDomainNames.size()));
    }

    Date randomTime(Date from, Date to) {
        Random random = new Random();
        Date date = new Date();
        date.setTime(from.getTime() + (long) ((to.getTime() - from.getTime()) * random.nextDouble()));
        return date;
    }

    <C extends Collection<K>, K> C randomCollection(C a) {
        Collection<K> collection = null;
        if (a instanceof Set) {
            collection = new HashSet<>();
        } else if (a instanceof List) {
            collection = new ArrayList<>();
        }

        for (K k : a) {
            if (randomBoolean()) {
                collection.add(k);
            }
        }
        return (C) collection;
    }
}
