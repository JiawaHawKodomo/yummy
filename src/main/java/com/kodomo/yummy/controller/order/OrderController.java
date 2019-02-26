package com.kodomo.yummy.controller.order;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-26 14:14
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @GetMapping("/{orderId}")
    public String order(HttpServletRequest request, @PathVariable("orderId") Integer orderId) {
        if (request.getSession(true).getAttribute("customer") != null) {
            return "redirect:/customer/order/" + orderId;
        } else if (request.getSession(true).getAttribute("restaurant") != null) {
            return "redirect:/restaurant/order/" + orderId;
        }
        return "/customer/login";
    }

}
