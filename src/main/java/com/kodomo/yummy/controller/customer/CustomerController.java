package com.kodomo.yummy.controller.customer;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.entity.customer.Customer;
import com.kodomo.yummy.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @Value("${lbs.key}")
    private String lbsKey;

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
                request.getSession().setAttribute("customer", customer.getEmail());
                result.put("result", true);
            }
        } catch (ParamErrorException e) {
            //输入不正确
            result.put("result", false);
            result.put("info", e.getMessage());
        }

        return result;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession(true).setAttribute("customer", null);
        return "redirect:/";
    }


    /**
     * 个人信息
     *
     * @param request request
     * @param model
     * @return a
     */
    @GetMapping("/info")
    public String info(HttpServletRequest request, Model model) {
        model.addAttribute("lbsKey", lbsKey);
        String sessionCustomer = (String) request.getSession(true).getAttribute("customer");
        Customer customer = customerBlService.getCustomerEntityByEmail(sessionCustomer);
        model.addAttribute("customer", customer);
        return "customer/info";
    }

    /**
     * 充值
     *
     * @return
     */
    @GetMapping("/recharge")
    public String recharge(HttpServletRequest request, Model model) {
        String customerEmail = (String) request.getSession(true).getAttribute("customer");
        Customer customer = customerBlService.getCustomerEntityByEmail(customerEmail);
        model.addAttribute("customer", customer);
        return "customer/recharge";
    }

    @ResponseBody
    @PostMapping("/recharge")
    public Map<String, Object> recharge(HttpServletRequest request,
                                        @RequestParam("amount") Double amount) {
        Map<String, Object> result = new HashMap<>();
        String customerEmail = (String) request.getSession(true).getAttribute("customer");

        try {
            customerBlService.recharge(customerEmail, amount);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", "参数错误:" + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", "用户不存在");
        } catch (UnupdatableException e) {
            result.put("info", "当前状态无法充值:" + e.getCurrentStateText());
        }
        return result;
    }
}
