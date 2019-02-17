package com.kodomo.yummy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-17 15:46
 */
@Controller
@RequestMapping("/management")
public class ManagementController {

    @GetMapping("/login")
    public String managementLogin() {
        return "management/login";
    }

}
