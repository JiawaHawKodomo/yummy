package com.kodomo.yummy.controller;

import com.kodomo.yummy.bl.CustomerBlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-16 9:14
 */
@Controller
public class IndexController {

    private final CustomerBlService customerBlService;

    @Autowired
    public IndexController(CustomerBlService customerBlService) {
        this.customerBlService = customerBlService;
    }

    @GetMapping("/")
    public String index(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("customer") != null) {
            return "redirect:/customer/place";
        } else if (session.getAttribute("restaurant") != null) {
            return "redirect:/restaurant";
        } else {
            return "index";
        }
    }

    @GetMapping("/activate/{code}")
    public String activate(@PathVariable("code") String code, Model model) {
        model.addAttribute("state", customerBlService.activate(code));
        return "customer/activate";
    }
}
