package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-18 10:46
 */
@Repository
public interface ManagerDao extends JpaRepository<Manager, String> {

}
