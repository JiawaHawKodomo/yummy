package com.kodomo.yummy.controller.customer;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.controller.vo.OrderVo;
import com.kodomo.yummy.entity.Customer;
import com.kodomo.yummy.entity.Order;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
            result.put("info", "未登录");
            return result;
        }

        try {
            Order order = orderBlService.createNewOrder(email, orderVo);
            result.put("result", order.getOrderId());
        } catch (ParamErrorException e) {
            result.put("info", "参数不正确:" + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", "用户不存在:" + e.getMessage());
        } catch (UnupdatableException e) {
            result.put("info", "状态不正确, 无法创建订单, 当前状态:" + e.getCurrentStateText());
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
            result.put("info", "未登录");
            return result;
        }

        try {
            orderBlService.payOrder(email, password, orderId);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", "参数错误:" + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", "用户不存在:" + email);
        } catch (NoSuchAttributeException e) {
            result.put("info", "不存在该订单");
        } catch (OrderTimeOutException e) {
            result.put("info", "该订单已超时, 无法完成支付, 订单创建时间:" + e.getCreateTime());
        } catch (LackOfBalanceException e) {
            result.put("info", "余额不足, 请充值后重试");
        } catch (UnupdatableException e) {
            result.put("info", "订单状态不正确,无法支付");
        } catch (PasswordErrorException e) {
            result.put("info", "密码不正确");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/order/confirm", method = RequestMethod.PUT)
    public Map<String, Object> orderConfirm(HttpServletRequest request, @RequestParam("orderId") Integer orderId) {
        Map<String, Object> result = new HashMap<>();
        String email = (String) request.getSession(true).getAttribute("customer");
        if (email == null) {
            result.put("info", "未登录");
            return result;
        }

        try {
            orderBlService.customerConfirmOrder(email, orderId);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", "参数错误:" + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", "用户不存在");
        } catch (NoSuchAttributeException e) {
            result.put("info", "订单不存在");
        } catch (UnupdatableException e) {
            result.put("info", "订单状态不正确, 无法确认, 当前状态:" + e.getCurrentStateText());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/order/cancel", method = RequestMethod.PUT)
    public Map<String, Object> orderCancel(HttpServletRequest request, @RequestParam("orderId") Integer orderId) {
        Map<String, Object> result = new HashMap<>();
        String email = (String) request.getSession(true).getAttribute("customer");
        if (email == null) {
            result.put("info", "未登录");
            return result;
        }

        try {
            orderBlService.customerCancelOrder(email, orderId);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", "参数错误:" + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", "用户不存在");
        } catch (NoSuchAttributeException e) {
            result.put("info", "订单不存在");
        } catch (UnupdatableException e) {
            result.put("info", "订单状态不正确, 无法取消, 当前状态:" + e.getCurrentStateText());
        }
        return result;
    }
}
