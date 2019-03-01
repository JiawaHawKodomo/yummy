package com.kodomo.yummy.controller.management;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.bl.ManagementBlService;
import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.controller.vo.OrderRefundStrategyVo;
import com.kodomo.yummy.controller.vo.OrderSettlementStrategyVo;
import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    private final CustomerBlService customerBlService;

    @Autowired
    public ManagementController(ManagementBlService managementBlService, RestaurantBlService restaurantBlService, OrderBlService orderBlService, CustomerBlService customerBlService) {
        this.managementBlService = managementBlService;
        this.restaurantBlService = restaurantBlService;
        this.orderBlService = orderBlService;
        this.customerBlService = customerBlService;
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
            request.getSession(true).setAttribute("manager", manager.getManagerId());
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
    public String management(HttpServletRequest request, Model model) {
        model.addAttribute("manager", managementBlService.getManagerById((String) request.getSession(true).getAttribute("manager")));
        model.addAttribute("unactivatedRestaurant", restaurantBlService.getRestaurantByState(UserState.UNACTIVATED));
        model.addAttribute("currentOrderSettlementStrategy", orderBlService.getCurrentOrderSettlementStrategy());
        model.addAttribute("currentOrderRefundStrategy", orderBlService.getCurrentOrderRefundStrategy());
        model.addAttribute("currentCustomerLevelStrategy", customerBlService.getCurrentCustomerLevelStrategy());
        model.addAttribute("modificationInfos", restaurantBlService.getWaitingRestaurantModificationInfo());
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

    @PutMapping("/restaurantModificationConfirm")
    @ResponseBody
    public Map<String, Object> restaurantModificationConfirm(HttpServletRequest request
            , @RequestParam("pass") Boolean pass
            , @RequestParam("id") Integer modificationId) {
        Map<String, Object> result = new HashMap<>();
        if (request.getSession(true).getAttribute("manager") == null) {
            result.put("info", "请先登录");
            return result;
        }

        try {
            restaurantBlService.confirmModification(modificationId, pass);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", "参数错误:" + e.getErrorFieldsInfo());
        } catch (DuplicatedUniqueKeyException e) {
            result.put("info", "电话已经被注册, 无法修改");
        } catch (NoSuchAttributeException e) {
            result.put("info", "不存在该修改信息");
        }

        return result;
    }
}

