package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.RestaurantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-17 23:43
 */
@Repository
public interface RestaurantTypeDao extends JpaRepository<RestaurantType, Integer> {


}
