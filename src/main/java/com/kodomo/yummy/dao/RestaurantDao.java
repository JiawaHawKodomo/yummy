package com.kodomo.yummy.dao;

import com.kodomo.yummy.entity.Location;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.util.Utility;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-13 14:06
 */
@Repository
public interface RestaurantDao extends JpaRepository<Restaurant, Integer>, JpaSpecificationExecutor<Restaurant> {

    default Restaurant find(Integer id) {
        return findById(Utility.integer(id)).orElse(null);
    }

    default List<Restaurant> getRestaurantWithinSquare(Double lat, Double lng, Double distanceToSide) {
        try {
            return findAll(new Specification<Restaurant>() {
                @Nullable
                @Override
                public Predicate toPredicate(Root<Restaurant> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    Join<Restaurant, Location> locationJoin = root.join("location");
                    double latitudeByDistance = Utility.latitudeByDistance(distanceToSide);
                    double longitudeByDistance = Utility.longitudeByDisrance(distanceToSide, lat);
                    return criteriaBuilder.and(
                            criteriaBuilder.between(locationJoin.get("lat"), lat - latitudeByDistance, lat + latitudeByDistance),
                            criteriaBuilder.between(locationJoin.get("lng"), lng - longitudeByDistance, lng + longitudeByDistance)
                    );
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
