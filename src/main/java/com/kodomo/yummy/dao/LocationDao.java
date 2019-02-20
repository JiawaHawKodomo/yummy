package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-20 13:22
 */
@Repository
public interface LocationDao extends JpaRepository<Location, Integer> {

}
