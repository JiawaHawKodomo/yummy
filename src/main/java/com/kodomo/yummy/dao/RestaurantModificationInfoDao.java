package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.RestaurantModificationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-28 18:19
 */
@Repository
public interface RestaurantModificationInfoDao extends JpaRepository<RestaurantModificationInfo, Integer> {


}
