package com.kodomo.yummy.controller;

import com.kodomo.yummy.bl.ManagementBlService;
import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.exceptions.UnupdatableException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private final OrderBlService orderBlService;

    @Autowired
    public ManagementController(ManagementBlService managementBlService, RestaurantBlService restaurantBlService, OrderBlService orderBlService) {
        this.managementBlService = managementBlService;
        this.restaurantBlService = restaurantBlService;
        this.orderBlService = orderBlService;
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

    /**
     * 主页面
     *
     * @param model
     * @return
     */
    @RequestMapping
    public String management(Model model) {
        model.addAttribute("unactivatedRestaurant", restaurantBlService.getRestaurantByState(UserState.UNACTIVATED));
        model.addAttribute("currentOrderSettlementStrategy", orderBlService.getCurrentOrderSettlementStrategy());
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

    @PostMapping("/orderStrategy")
    @ResponseBody
    public Map<String, Object> updateOrderStrategy(HttpServletRequest request, @RequestParam Map<String, String> map) {
        Map<String, Object> result = new HashMap<>();
        try {
            Manager manager = (Manager) request.getSession(true).getAttribute("manager");
            if (manager == null || manager.getManagerId() == null) {
                //不存在
                throw new Exception("未登录");
            }

            String json = map.get("jsonData");
            JsonParser jsonParser = new JacksonJsonParser();
            List list = jsonParser.parseList(json);

            orderBlService.saveNewOrderSettlementStrategy(new ArrayList<>(list), manager);
            result.put("result", true);
        } catch (Exception e) {
            result.put("info", e.getMessage());
        } finally {
            return result;
        }
    }
}

