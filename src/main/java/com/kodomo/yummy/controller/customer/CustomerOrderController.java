package com.kodomo.yummy.controller.customer;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.entity.Customer;
import com.kodomo.yummy.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-20 18:59
 */
@Controller
@RequestMapping("/customer")
public class CustomerOrderController {

    private final CustomerBlService customerBlService;

    @Autowired
    public CustomerOrderController(CustomerBlService customerBlService) {
        this.customerBlService = customerBlService;
    }

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

}
