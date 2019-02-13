package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-13 12:41
 */
@Repository
public interface CustomerDao extends JpaRepository<Customer, String> {

}
