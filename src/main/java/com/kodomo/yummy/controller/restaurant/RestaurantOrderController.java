package com.kodomo.yummy.controller.restaurant;

import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import com.kodomo.yummy.exceptions.NoSuchAttributeException;
import com.kodomo.yummy.exceptions.ParamErrorException;
import com.kodomo.yummy.exceptions.UnupdatableException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${yummy-system.text.public.password-error}")
    private String passwordErrorText;
    @Value("${yummy-system.text.public.parameter-error}")
    private String parameterErrorText;
    @Value("${yummy-system.text.public.user-not-exists-error}")
    private String userNotExistsErrorText;
    @Value("${yummy-system.text.public.state-error}")
    private String stateErrorText;
    @Value("${yummy-system.text.public.not-login-error}")
    private String notLoginErrorText;
    @Value("${yummy-system.text.order.no-such-order-error}")
    private String orderDoesNotExistsText;

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
            result.put("info", notLoginErrorText);
            return result;
        }

        try {
            orderBlService.restaurantConfirmOrder(rid, orderId);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText);
        } catch (NoSuchAttributeException e) {
            result.put("info", orderDoesNotExistsText);
        } catch (UnupdatableException e) {
            result.put("info", stateErrorText + ":" + e.getCurrentStateText());
        }
        return result;
    }
}
