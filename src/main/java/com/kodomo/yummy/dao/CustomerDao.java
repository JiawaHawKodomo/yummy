package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.customer.Customer;
import com.kodomo.yummy.util.Utility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-13 12:41
 */
@Repository
public interface CustomerDao extends JpaRepository<Customer, String> {

    default Customer find(String id) {
        return findById(Utility.string(id)).orElse(null);
    }
}
