package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.CustomerRechargeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-23 22:36
 */
@Repository
public interface CustomerRechargeLogDao extends JpaRepository<CustomerRechargeLog, Integer> {

}
