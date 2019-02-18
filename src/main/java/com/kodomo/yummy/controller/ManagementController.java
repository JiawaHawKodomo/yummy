package com.kodomo.yummy.controller;

import com.kodomo.yummy.bl.ManagementBlService;
import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.entity.UserState;
import com.kodomo.yummy.exceptions.UnupdatableException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-17 15:46
 */
@Controller
@RequestMapping("/management")
public class ManagementController {

    private final ManagementBlService managementBlService;
    private final RestaurantBlService restaurantBlService;

    @Autowired
    public ManagementController(ManagementBlService managementBlService, RestaurantBlService restaurantBlService) {
        this.managementBlService = managementBlService;
        this.restaurantBlService = restaurantBlService;
    }

    @GetMapping("/login")
    public String managementLogin() {
        return "management/managementLogin";
    }

    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(HttpServletRequest request) {
        String id = request.getParameter("id");
        String password = request.getParameter("password");
        Map<String, Object> result = new HashMap<>();

        Manager manager = managementBlService.login(id, password);
        if (manager != null) {
            request.getSession(true).setAttribute("manager", manager);
            result.put("result", true);
        }
        return result;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        //清空session信息
        request.getSession(true).setAttribute("manager", null);
        return "redirect:/management/login";
    }

    @RequestMapping
    public String management(Model model) {
        model.addAttribute("unactivatedRestaurant", restaurantBlService.getRestaurantByState(UserState.UNACTIVATED));
        return "management/managementInfo";
    }

    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> register(HttpServletRequest request) {
        String id = request.getParameter("id");
        String password = request.getParameter("password");
        Map<String, Object> result = new HashMap<>();

        Manager manager = managementBlService.register(id, password);
        if (manager != null) {
            result.put("result", true);
        }
        return result;
    }

    @PostMapping("/approve")
    @ResponseBody
    public Map<String, Object> approve(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        if (request.getSession(true).getAttribute("manager") == null) {
            result.put("info", "请先登录");
            return result;
        }

        try {
            String id = request.getParameter("id");
            boolean pass = Boolean.parseBoolean(request.getParameter("pass"));
            if (managementBlService.approveRestaurant(id, pass)) {
                result.put("result", true);
            } else {
                result.put("info", "未知错误");
            }
        } catch (UserNotExistsException | UnupdatableException e) {
            result.put("info", e.getMessage());
        } catch (Exception e) {
            result.put("info", "未知错误");
        }

        return result;
    }
}
