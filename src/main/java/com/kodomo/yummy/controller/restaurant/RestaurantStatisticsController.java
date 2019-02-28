package com.kodomo.yummy.controller.restaurant;

import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.controller.vo.OrderStatisticsInfoVo;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-27 10:08
 */
@Controller
@RequestMapping("/restaurant")
public class RestaurantStatisticsController {

    private final RestaurantBlService restaurantBlService;

    @Autowired
    public RestaurantStatisticsController(RestaurantBlService restaurantBlService) {
        this.restaurantBlService = restaurantBlService;
    }

    @GetMapping("/statistics")
    public String statistics(HttpServletRequest request, Model model) {
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        if (rid == null) {
            return "redirect:/restaurant/login";
        }

        model.addAttribute("restaurant", restaurantBlService.getRestaurantById(rid));
        return "restaurant/statistics";
    }

    @ResponseBody
    @GetMapping("/statistics/data")
    public List<OrderStatisticsInfoVo> getStatisticsData(HttpServletRequest request) {
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        return restaurantBlService.getStatisticsInfos(rid);
    }

    @GetMapping("/statistics/time/{time}")
    public String statisticTime(HttpServletRequest request, Model model,
                                @PathVariable("time") String time,
                                @RequestParam("format") String format) {
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        model.addAttribute("restaurant", restaurantBlService.getRestaurantById(rid));
        model.addAttribute("orders", restaurantBlService.getOrdersByTimeOfCustomer(rid, time, format));
        return "restaurant/statisticsOrder";
    }
}
