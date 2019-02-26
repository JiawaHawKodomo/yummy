package com.kodomo.yummy.controller.customer;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.controller.vo.OrderStatisticsInfoVo;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-26 14:34
 */
@Controller
@RequestMapping("/customer")
public class CustomerStatisticsController {

    private final CustomerBlService customerBlService;

    @Autowired
    public CustomerStatisticsController(CustomerBlService customerBlService) {
        this.customerBlService = customerBlService;
    }

    @GetMapping("/statistics")
    public String statisticIndex(HttpServletRequest request, Model model) {
        String email = (String) request.getSession(true).getAttribute("customer");
        model.addAttribute("customer", customerBlService.getCustomerEntityByEmail(email));
        return "customer/statistics";
    }

    @ResponseBody
    @GetMapping("/statistics/data")
    public List<OrderStatisticsInfoVo> getStatisticData(HttpServletRequest request) {
        String email = (String) request.getSession(true).getAttribute("customer");
        try {
            return customerBlService.getStatisticsInfos(email);
        } catch (UserNotExistsException e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("/statistics/restaurant/{rid}")
    public String statisticRestaurant(HttpServletRequest request, Model model,
                                      @PathVariable("rid") Integer rid) {
        String email = (String) request.getSession(true).getAttribute("customer");
        model.addAttribute("customer", customerBlService.getCustomerEntityByEmail(email));
        try {
            model.addAttribute("orders", customerBlService.getOrdersByRestaurantOfCustomer(email, rid));
        } catch (UserNotExistsException ignored) {
        }
        return "customer/statisticsOrder";
    }

    @GetMapping("/statistics/time/{time}")
    public String statisticTime(HttpServletRequest request, Model model,
                                @PathVariable("time") String time,
                                @RequestParam("format") String format) {
        String email = (String) request.getSession(true).getAttribute("customer");
        model.addAttribute("customer", customerBlService.getCustomerEntityByEmail(email));

        try {
            model.addAttribute("orders", customerBlService.getOrdersByTimeOfCustomer(email, time, format));
        } catch (UserNotExistsException ignored) {
        }
        return "customer/statisticsOrder";
    }
}
