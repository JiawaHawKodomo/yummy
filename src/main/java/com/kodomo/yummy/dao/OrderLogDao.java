package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-23 17:09
 */
public interface OrderLogDao extends JpaRepository<OrderLog, Integer> {


}
