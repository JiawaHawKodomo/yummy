package com.kodomo.yummy.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-16 10:04
 */
@Data
@Entity
@Table(name = "location_info")
public class Location {

    @Id
    @Column(name = "location_id")
    @GeneratedValue(generator = "generator_lct")
    @GenericGenerator(name = "generator_lct", strategy = "native")
    private Integer locationId;
    private String city;
    @Column(name = "block_info")
    private String blockInfo;
    @Column(name = "point_info")
    private String pointInfo;
    private Double lat;//纬度
    private Double lng;//经度
    private String note;

    private static final double EARTH_RADIUS = 6378137.0;//赤道半径(单位m)

    /**
     * 返回两个地址的距离, 用米表示
     *
     * @param location
     * @return
     */
    public Double distanceBetween(Location location) {
        if (location == null || location.getLat() == null || location.getLng() == null) {
            return Double.MAX_VALUE;
        } else {
            double radLat1 = rad(lat);
            double radLat2 = rad(location.getLat());

            double radLon1 = rad(lng);
            double radLon2 = rad(location.getLng());

            if (radLat1 < 0)
                radLat1 = Math.PI / 2 + Math.abs(radLat1);// south
            if (radLat1 > 0)
                radLat1 = Math.PI / 2 - Math.abs(radLat1);// north
            if (radLon1 < 0)
                radLon1 = Math.PI * 2 - Math.abs(radLon1);// west
            if (radLat2 < 0)
                radLat2 = Math.PI / 2 + Math.abs(radLat2);// south
            if (radLat2 > 0)
                radLat2 = Math.PI / 2 - Math.abs(radLat2);// north
            if (radLon2 < 0)
                radLon2 = Math.PI * 2 - Math.abs(radLon2);// west
            double x1 = EARTH_RADIUS * Math.cos(radLon1) * Math.sin(radLat1);
            double y1 = EARTH_RADIUS * Math.sin(radLon1) * Math.sin(radLat1);
            double z1 = EARTH_RADIUS * Math.cos(radLat1);

            double x2 = EARTH_RADIUS * Math.cos(radLon2) * Math.sin(radLat2);
            double y2 = EARTH_RADIUS * Math.sin(radLon2) * Math.sin(radLat2);
            double z2 = EARTH_RADIUS * Math.cos(radLat2);

            double d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2));
            //余弦定理求夹角
            double theta = Math.acos((EARTH_RADIUS * EARTH_RADIUS + EARTH_RADIUS * EARTH_RADIUS - d * d) / (2 * EARTH_RADIUS * EARTH_RADIUS));
            return theta * EARTH_RADIUS;
        }
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public String getInfo() {
        return city + blockInfo + pointInfo + note;
    }
}
