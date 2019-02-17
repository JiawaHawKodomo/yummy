package com.kodomo.yummy.controller;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.entity.Customer;
import com.kodomo.yummy.exceptions.ParamErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 顾客相关的页面
 *
 * @author Shuaiyu Yao
 * @create 2019-02-16 11:53
 */
@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerBlService customerBlService;

    @Autowired
    public CustomerController(CustomerBlService customerBlService) {
        this.customerBlService = customerBlService;
    }

    @GetMapping("/login")
    public String customerLogin() {
        return "customer/login";
    }

    @ResponseBody
    @PostMapping("/login")
    public Map<String, Object> customerLoginRequest(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        System.out.println(password);
        Map<String, Object> result = new HashMap<>();
        try {
            Customer customer = customerBlService.loginOrRegister(email, password);
            if (customer == null) {
                result.put("result", false);
                result.put("info", "账号或密码不正确");
            } else {
                request.getSession().setAttribute("customer", customer);
                result.put("result", true);
            }
        } catch (ParamErrorException e) {
            //输入不正确
            result.put("result", false);
            result.put("info", e.getMessage());
        }

        return result;
    }

    /**
     * 选择地址
     *
     * @return
     */
    @GetMapping("/place")
    public String place() {
        return "customer/place";
    }


}
