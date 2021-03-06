package com.kodomo.yummy.controller.restaurant;

import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import com.kodomo.yummy.exceptions.DuplicatedUniqueKeyException;
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
 * @create 2019-02-20 19:36
 **/
@RequestMapping("/restaurant")
@Controller
public class RestaurantRegisterController {

    @Value("${yummy-system.text.public.parameter-error}")
    private String parameterErrorText;
    @Value("${yummy-system.text.public.telephone-duplicated}")
    private String telephoneDuplicatedText;

    @Value("${lbs.key}")
    private String lbsKey;
    private final RestaurantBlService restaurantBlService;

    @Autowired
    public RestaurantRegisterController(RestaurantBlService restaurantBlService) {
        this.restaurantBlService = restaurantBlService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("lbsKey", lbsKey);
        return "restaurant/register";
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
            request.getSession().setAttribute("restaurant", restaurant.getRestaurantId());
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (DuplicatedUniqueKeyException e) {
            result.put("info", telephoneDuplicatedText);
        }
        return result;
    }

}
