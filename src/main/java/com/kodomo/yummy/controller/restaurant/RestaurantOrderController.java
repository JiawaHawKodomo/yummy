package com.kodomo.yummy.controller.restaurant;

import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import com.kodomo.yummy.exceptions.NoSuchAttributeException;
import com.kodomo.yummy.exceptions.ParamErrorException;
import com.kodomo.yummy.exceptions.UnupdatableException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-26 10:09
 */
@Controller
@RequestMapping("/restaurant")
public class RestaurantOrderController {

    private final RestaurantBlService restaurantBlService;
    private final OrderBlService orderBlService;

    @Autowired
    public RestaurantOrderController(RestaurantBlService restaurantBlService, OrderBlService orderBlService) {
        this.restaurantBlService = restaurantBlService;
        this.orderBlService = orderBlService;
    }

    @GetMapping("/order")
    public String orders(HttpServletRequest request, Model model) {
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        Restaurant restaurant = restaurantBlService.getRestaurantById(rid);
        model.addAttribute("restaurant", restaurant);
        return "restaurant/orders";
    }

    @GetMapping("/order/{orderId}")
    public String orderInfo(HttpServletRequest request, @PathVariable("orderId") Integer orderId
            , Model model) {
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        Restaurant restaurant = restaurantBlService.getRestaurantById(rid);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("order", restaurant.getOrderById(orderId));
        return "restaurant/orderInfo";
    }

    @RequestMapping(value = "/order/confirm", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> orderConfirm(HttpServletRequest request,
                                            @RequestParam("orderId") Integer orderId) {
        Map<String, Object> result = new HashMap<>();
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        if (rid == null) {
            result.put("info", "未登录");
            return result;
        }

        try {
            orderBlService.restaurantConfirmOrder(rid, orderId);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", "参数错误:" + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", "用户不存在");
        } catch (NoSuchAttributeException e) {
            result.put("info", "订单不存在");
        } catch (UnupdatableException e) {
            result.put("info", "订单状态不正确, 无法修改, 状态:" + e.getCurrentStateText());
        }
        return result;
    }
}
