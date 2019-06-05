package com.kodomo.yummy.controller.customer;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.controller.vo.OrderVo;
import com.kodomo.yummy.entity.customer.Customer;
import com.kodomo.yummy.entity.order.Order;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import com.kodomo.yummy.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-20 18:59
 */
@Controller
@RequestMapping("/customer")
public class CustomerOrderController {

    @Value("${yummy-system.text.public.parameter-error}")
    private String parameterErrorText;
    @Value("${yummy-system.text.public.user-not-exists-error}")
    private String userNotExistsErrorText;
    @Value("${yummy-system.text.public.state-error}")
    private String stateErrorText;
    @Value("${yummy-system.text.public.not-login-error}")
    private String notLoginErrorText;
    @Value("${yummy-system.text.restaurant.has-closed}")
    private String restaurantHasClosedText;
    @Value("${yummy-system.text.order.no-such-order-error}")
    private String orderDoesNotExistText;
    @Value("${yummy-system.text.order.time-out}")
    private String orderTimeOutText;
    @Value("${yummy-system.text.customer.lack-of-balance}")
    private String lackOfBalanceText;
    @Value("${yummy-system.text.customer.pay-password-error}")
    private String payPasswordErrorText;
    @Value("${yummy-system.text.order.exceed-remaining}")
    private String exceedRemainingText;

    private final CustomerBlService customerBlService;
    private final RestaurantBlService restaurantBlService;
    private final OrderBlService orderBlService;

    @Autowired
    public CustomerOrderController(CustomerBlService customerBlService, RestaurantBlService restaurantBlService, OrderBlService orderBlService) {
        this.customerBlService = customerBlService;
        this.restaurantBlService = restaurantBlService;
        this.orderBlService = orderBlService;
    }

    /**
     * 选择地址
     *
     * @return
     */
    @GetMapping("/place")
    public String place(HttpServletRequest request, Model model,
                        @RequestParam(value = "location_id", required = false) Integer locationId,
                        @RequestParam(value = "search", required = false) String search) {
        String email = (String) request.getSession(true).getAttribute("customer");
        Customer customer = customerBlService.getCustomerEntityByEmail(email);
        model.addAttribute("customer", customer);
        //选择地址, 查找
        if (locationId != null) {
            try {
                model.addAttribute("locationId", locationId);
                if (search == null) {
                    List<Restaurant> restaurants = customerBlService.getRestaurantWithinDistributionDistance(email, locationId);
                    model.addAttribute("resultRestaurants", restaurants);
                } else {
                    model.addAttribute("keyWord", search);
                    List<Restaurant> restaurants = customerBlService.getSearchedRestaurant(email, locationId, search);
                    model.addAttribute("resultRestaurants", restaurants);
                }
            } catch (ParamErrorException | NoSuchAttributeException | UserNotExistsException ignored) {
            }
        }
        return "customer/place";
    }

    /**
     * 点单时页面
     *
     * @param request
     * @param model
     * @param restaurantId
     * @return
     */
    @GetMapping("/ordering/{restaurant_id}")
    public String ordering(HttpServletRequest request, Model model,
                           @PathVariable("restaurant_id") Integer restaurantId,
                           @RequestParam(value = "location_id", required = false) Integer locationId) {
        String email = (String) request.getSession(true).getAttribute("customer");
        Customer customer = customerBlService.getCustomerEntityByEmail(email);
        model.addAttribute("customer", customer);
        Restaurant restaurant = restaurantBlService.getRestaurantById(restaurantId);
        model.addAttribute("restaurant", restaurant);
        if (customer == null || restaurant == null) {
            return "redirect:/customer";
        }

        if (locationId != null) {
            model.addAttribute("locationId", locationId);
        }
        return "restaurant/info";
    }

    /**
     * 创建订单
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/order")
    public Map<String, Object> submitOrder(HttpServletRequest request,
                                           @RequestBody OrderVo orderVo) {
        Map<String, Object> result = new HashMap<>();
        String email = (String) request.getSession(true).getAttribute("customer");
        if (email == null) {
            result.put("info", notLoginErrorText);
            return result;
        }

        try {
            Order order = orderBlService.createNewOrder(email, orderVo, new Date());
            result.put("result", order.getOrderId());
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText + e.getMessage());
        } catch (UnupdatableException e) {
            result.put("info", stateErrorText + ":" + e.getCurrentStateText());
        } catch (RestaurantHasClosedException e) {
            result.put("info", restaurantHasClosedText);
        } catch (ExceedRemainException e) {
            result.put("info", exceedRemainingText + ":" + e.getOfferingNames());
        }

        return result;
    }

    /**
     * 订单页
     *
     * @param request request
     * @param model   model
     * @return a
     */
    @GetMapping("/order")
    public String order(HttpServletRequest request, Model model) {
        String sessionCustomer = (String) request.getSession(true).getAttribute("customer");
        Customer customer = customerBlService.getCustomerEntityByEmail(sessionCustomer);
        model.addAttribute("customer", customer);
        return "customer/orders";
    }

    /**
     * 订单详情页
     *
     * @param orderId
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/order/{orderId}")
    public String orderInfo(@PathVariable("orderId") Integer orderId, HttpServletRequest request, Model model) {
        String path = "customer/orderInfo";
        String customerEmail = (String) request.getSession(true).getAttribute("customer");
        Customer customer = customerBlService.getCustomerEntityByEmail(customerEmail);
        if (customer == null) {
            return path;
        }

        model.addAttribute("customer", customer);
        model.addAttribute("order", customer.getOrderByOrderId(orderId));
        return path;
    }

    @ResponseBody
    @RequestMapping(value = "/order/pay", method = RequestMethod.PUT)
    public Map<String, Object> orderPay(HttpServletRequest request, @RequestParam("orderId") Integer orderId, @RequestParam("password") String password) {
        Map<String, Object> result = new HashMap<>();
        String email = (String) request.getSession(true).getAttribute("customer");
        if (email == null) {
            result.put("info", notLoginErrorText);
            return result;
        }

        try {
            orderBlService.payOrder(email, password, orderId, new Date());
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText + email);
        } catch (NoSuchAttributeException e) {
            result.put("info", orderDoesNotExistText);
        } catch (OrderTimeOutException e) {
            result.put("info", orderTimeOutText);
        } catch (LackOfBalanceException e) {
            result.put("info", lackOfBalanceText);
        } catch (UnupdatableException e) {
            result.put("info", stateErrorText);
        } catch (PasswordErrorException e) {
            result.put("info", payPasswordErrorText);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/order/confirm", method = RequestMethod.PUT)
    public Map<String, Object> orderConfirm(HttpServletRequest request, @RequestParam("orderId") Integer orderId) {
        Map<String, Object> result = new HashMap<>();
        String email = (String) request.getSession(true).getAttribute("customer");
        if (email == null) {
            result.put("info", notLoginErrorText);
            return result;
        }

        try {
            orderBlService.customerConfirmOrder(email, orderId, new Date());
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText);
        } catch (NoSuchAttributeException e) {
            result.put("info", orderDoesNotExistText);
        } catch (UnupdatableException e) {
            result.put("info", stateErrorText + ":" + e.getCurrentStateText());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/order/cancel", method = RequestMethod.PUT)
    public Map<String, Object> orderCancel(HttpServletRequest request, @RequestParam("orderId") Integer orderId) {
        Map<String, Object> result = new HashMap<>();
        String email = (String) request.getSession(true).getAttribute("customer");
        if (email == null) {
            result.put("info", notLoginErrorText);
            return result;
        }

        try {
            orderBlService.customerCancelOrder(email, orderId);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText);
        } catch (NoSuchAttributeException e) {
            result.put("info", orderDoesNotExistText);
        } catch (UnupdatableException e) {
            result.put("info", stateErrorText + ":" + e.getCurrentStateText());
        }
        return result;
    }
}
