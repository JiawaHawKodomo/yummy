package com.kodomo.yummy.bl.restaurant;

import com.kodomo.yummy.dao.RestaurantDao;
import com.kodomo.yummy.dao.RestaurantMessageDao;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import com.kodomo.yummy.entity.restaurant.RestaurantMessage;
import com.kodomo.yummy.exceptions.ParamErrorException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-03-02 14:01
 */
@Component
public class RestaurantMessageHelper {

    private final RestaurantMessageDao restaurantMessageDao;
    private final RestaurantDao restaurantDao;

    @Autowired
    public RestaurantMessageHelper(RestaurantMessageDao restaurantMessageDao, RestaurantDao restaurantDao) {
        this.restaurantMessageDao = restaurantMessageDao;
        this.restaurantDao = restaurantDao;
    }

    /**
     * 发送消息
     *
     * @param restaurantId
     * @param content
     */
    public void sendMessage(Integer restaurantId, String content) throws UserNotExistsException, ParamErrorException {
        Restaurant restaurant = restaurantDao.find(restaurantId);
        if (restaurant == null) {
            throw new UserNotExistsException();
        }

        if (content == null) {
            throw new ParamErrorException("消息内容");
        }

        RestaurantMessage message = new RestaurantMessage();
        message.setRestaurant(restaurant);
        message.setContent(content);

        restaurantMessageDao.save(message);
    }

    /**
     * 商家阅读消息
     *
     * @param restaurantId
     */
    void readMessage(Integer restaurantId) throws ParamErrorException {
        if (restaurantId == null) throw new ParamErrorException();
        List<RestaurantMessage> messages = restaurantMessageDao.getUnreadMessage(restaurantId);
        messages.forEach(restaurantMessage -> restaurantMessage.setHasBeenRead(true));
        restaurantMessageDao.saveAll(messages);
    }
}
