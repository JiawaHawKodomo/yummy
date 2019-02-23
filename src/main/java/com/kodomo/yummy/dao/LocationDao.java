package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.Location;
import com.kodomo.yummy.util.Utility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-20 13:22
 */
@Repository
public interface LocationDao extends JpaRepository<Location, Integer> {

    default Location find(Integer id) {
        return findById(Utility.integer(id)).orElse(null);
    }
}
