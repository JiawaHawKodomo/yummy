package com.kodomo.yummy.controller;

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

    @GetMapping
    public String restaurant() {
        return "restaurant/restaurantInfo";
    }

    @GetMapping("/login")
    public String restaurantLogin() {
        return "restaurant/restaurantLogin";
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

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("lbsKey", lbsKey);
        return "restaurant/restaurantRegister";
    }

    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> register(HttpServletRequest request, @RequestParam("name") String name,
                                        @RequestParam("password") String password,
                                        @RequestParam("tel") String tel,
                                        @RequestParam("time") String time,
                                        @RequestParam("type") String type,
                                        @RequestParam("note") String note,
                                        @RequestParam("city") String city,
                                        @RequestParam("lat") Double lat, @RequestParam("lng") Double lng,
                                        @RequestParam("poiaddress") String block,
                                        @RequestParam("poiname") String point,
                                        @RequestParam("addressNote") String addressNote) {

        Map<String, Object> result = new HashMap<>();
        try {
            Restaurant restaurant = restaurantBlService.registerRestaurant(name, password, tel, time, type, note, city, lat, lng, block, point, addressNote);
            result.put("result", true);
            result.put("restaurant", restaurant.getRestaurantId());
            //添加session信息
            request.getSession().setAttribute("restaurant", restaurant);
        } catch (ParamErrorException e) {
            result.put("info", "以下信息填写错误:" + e.getErrorFieldsInfo());
        } catch (DuplicatedPrimaryKeyException e) {
            result.put("info", "该电话已经被注册");
        }
        return result;
    }
}
