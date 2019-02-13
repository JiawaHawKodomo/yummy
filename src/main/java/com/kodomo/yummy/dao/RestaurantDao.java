package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-13 14:06
 */
@Repository
public interface RestaurantDao extends JpaRepository<Restaurant, String> {
}