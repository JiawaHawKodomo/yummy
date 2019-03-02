package com.kodomo.yummy.controller.restaurant;

import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.exceptions.ParamErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Shuaiyu Yao
 * @create 2019-03-02 13:49
 */
@Controller
@RequestMapping("/restaurant")
public class RestaurantMessageController {
    private final RestaurantBlService restaurantBlService;

    @Autowired
    public RestaurantMessageController(RestaurantBlService restaurantBlService) {
        this.restaurantBlService = restaurantBlService;
    }

    @GetMapping("/message")
    public String messageIndex(HttpServletRequest request, Model model) {
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        model.addAttribute("restaurant", restaurantBlService.getRestaurantById(rid));
        try {
            //阅读消息
            restaurantBlService.readMessage(rid);
        } catch (ParamErrorException ignored) {
        }
        return "restaurant/message";
    }

}
