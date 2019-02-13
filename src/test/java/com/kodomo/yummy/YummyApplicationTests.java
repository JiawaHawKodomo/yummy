package com.kodomo.yummy;

import com.kodomo.yummy.config.StaticConfig;
import com.kodomo.yummy.dao.CustomerDao;
import com.kodomo.yummy.entity.Customer;
import com.kodomo.yummy.entity.CustomerState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YummyApplicationTests {

    @Autowired
    private CustomerDao customerDao;

    @Test
    public void contextLoads() {
        System.out.println(StaticConfig.getMinPayment());
    }

}

