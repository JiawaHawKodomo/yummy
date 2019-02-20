package com.kodomo.yummy.controller.restaurant;

import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.exceptions.DuplicatedPrimaryKeyException;
import com.kodomo.yummy.exceptions.ParamErrorException;
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
 * @create 2019-02-17 11:19
 */
@Controller
@RequestMapping("/restaurant")
public class RestaurantController {

    @Value("${lbs.key}")
    private String lbsKey;

    private final RestaurantBlService restaurantBlService;

    @Autowired
    public RestaurantController(RestaurantBlService restaurantBlService) {
        this.restaurantBlService = restaurantBlService;
    }

    /**
     * 自己店铺的主页面
     *
     * @param request
     * @return
     */
    @GetMapping
    public String restaurantSelf(HttpServletRequest request) {
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        if (rid == null) {//未登录, 进入登录页面
            return "redirect:/restaurant/login";
        }
        return "redirect:/restaurant/index/" + rid;
    }

    /**
     * 主页面
     *
     * @param rid
     * @param model
     * @return
     */
    @GetMapping("/index/{rid}")
    public String restaurant(@PathVariable("rid") Integer rid, Model model) {
        Restaurant restaurant = restaurantBlService.getRestaurantById(rid);
        model.addAttribute("restaurant", restaurant);
        return "restaurant/info";
    }

    @GetMapping("/login")
    public String restaurantLogin(HttpServletRequest request) {
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        if (rid != null)//已登录, 进入主页面
            return "redirect:/restaurant/index/" + rid;
        return "restaurant/login";
    }

    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(HttpServletRequest request, @RequestParam("id") String id, @RequestParam("password") String password) {
        Map<String, Object> result = new HashMap<>();
        Restaurant restaurant = restaurantBlService.login(id, password);
        if (restaurant != null) {
            request.getSession(true).setAttribute("restaurant", restaurant.getRestaurantId());
            result.put("result", true);
        } else {
            result.put("result", false);
        }
        return result;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession(true).setAttribute("restaurant", null);
        return "redirect:/";
    }

    @GetMapping("/order")
    public String orders(HttpServletRequest request, Model model) {
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        Restaurant restaurant = restaurantBlService.getRestaurantById(rid);
        model.addAttribute("restaurant", restaurant);
        return "restaurant/orders";
    }

    @GetMapping("/manage")
    public String manage(HttpServletRequest request, Model model) {
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        Restaurant restaurant = restaurantBlService.getRestaurantById(rid);
        model.addAttribute("restaurant", restaurant);
        return "restaurant/manage";
    }
}
