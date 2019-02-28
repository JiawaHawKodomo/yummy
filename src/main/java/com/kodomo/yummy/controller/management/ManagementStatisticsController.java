package com.kodomo.yummy.controller.management;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.bl.ManagementBlService;
import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.controller.vo.CustomerRechargeStatisticsVo;
import com.kodomo.yummy.controller.vo.CustomerStatisticsVo;
import com.kodomo.yummy.controller.vo.OrderStatisticsInfoVo;
import com.kodomo.yummy.controller.vo.RestaurantStatisticsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-27 19:23
 */
@Controller
@RequestMapping("/management")
public class ManagementStatisticsController {

    private final ManagementBlService managementBlService;
    private final RestaurantBlService restaurantBlService;
    private final CustomerBlService customerBlService;
    private final OrderBlService orderBlService;

    @Autowired
    public ManagementStatisticsController(ManagementBlService managementBlService, RestaurantBlService restaurantBlService, CustomerBlService customerBlService, OrderBlService orderBlService) {
        this.managementBlService = managementBlService;
        this.restaurantBlService = restaurantBlService;
        this.customerBlService = customerBlService;
        this.orderBlService = orderBlService;
    }

    @GetMapping("/statistics")
    public String statisticIndex(HttpServletRequest request, Model model) {
        model.addAttribute("manager", managementBlService.getManagerById((String) request.getSession(true).getAttribute("manager")));
        return "management/managementStatistics";
    }

    /**
     * 获取餐厅统计信息
     *
     * @return
     */
    @GetMapping("/statistics/restaurant")
    @ResponseBody
    public List<RestaurantStatisticsVo> getRestaurantStatisticsInfo() {
        return restaurantBlService.getRestaurantStatisticsInfo();
    }

    /*
     * 获取顾客统计信息
     */
    @ResponseBody
    @GetMapping("/statistics/customer")
    public List<CustomerStatisticsVo> getCustomerStatisticsInfo() {
        return customerBlService.getCustomerStatisticsVo();
    }

    /**
     * 获取订单统计信息
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/statistics/order")
    public List<OrderStatisticsInfoVo> getOrderStatisticsVo() {
        return orderBlService.getOrderStatisticsVo();
    }

    /**
     * 获取充值信息
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/statistics/recharge")
    public List<CustomerRechargeStatisticsVo> getCustomerRechargeStatisticsVo() {
        return customerBlService.getCustomerRechargeStatisticsVo();
    }
}
