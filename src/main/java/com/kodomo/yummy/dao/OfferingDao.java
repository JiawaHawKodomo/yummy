package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.Offering;
import com.kodomo.yummy.util.Utility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-13 14:38
 */
@Repository
public interface OfferingDao extends JpaRepository<Offering, Integer> {

    default Offering find(Integer id) {
        return findById(Utility.integer(id)).orElse(null);
    }
}
