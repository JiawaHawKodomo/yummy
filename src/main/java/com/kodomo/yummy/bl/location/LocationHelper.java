package com.kodomo.yummy.bl.location;

import com.kodomo.yummy.entity.Location;
import com.kodomo.yummy.exceptions.ParamErrorException;
import org.springframework.stereotype.Service;

/**
 * 处理location行为
 *
 * @author Shuaiyu Yao
 * @create 2019-02-20 11:21
 */
@Service
public class LocationHelper {

    /**
     * 创建location
     *
     * @param block     block
     * @param point     point
     * @param note      note
     * @param city      city
     * @param telephone telephone
     * @param lat       lat
     * @param lng       lng
     * @return location
     */
    public Location createLocation(String block, String point, String note, String city, String telephone, Double lat, Double lng) throws ParamErrorException {
        if (block == null || point == null || city == null || lat == null || lng == null) {
            throw new ParamErrorException("location参数不正确:block:" + block + "point:" + point + "city:" + city + "lat:" + lat + "lng:" + lng);
        }

        //消除'我的位置'
        if (point.equals("我的位置")) {
            point = "";
        }

        Location location = new Location();
        location.setBlockInfo(block);
        location.setPointInfo(point);
        location.setNote(note);
        location.setCity(city);
        location.setTelephone(telephone);
        location.setLat(lat);
        location.setLng(lng);
        return location;
    }

}
