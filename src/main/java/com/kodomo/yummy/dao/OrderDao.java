package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * @author Shuaiyu Yao
 * @create 2019-02-13 15:01
 */
@Repository
public interface OrderDao extends JpaRepository<Order, Integer> {

    default Order find(Integer id) {
        if (id == null) return null;
        return findById(id).orElse(null);
    }
}
