package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.restaurant.RestaurantStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-22 13:51
 */
@Repository
public interface RestaurantStrategyDao extends JpaRepository<RestaurantStrategy, Integer> {

}
