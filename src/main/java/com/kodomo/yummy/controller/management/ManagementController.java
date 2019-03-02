package com.kodomo.yummy.controller.management;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.bl.ManagementBlService;
import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.entity.entity_enum.UserState;
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
 * @author Shuaiyu Yao
 * @create 2019-02-17 15:46
 */
@Controller
@RequestMapping("/management")
public class ManagementController {

    @Value("${yummy-system.text.public.password-error}")
    private String passwordErrorText;
    @Value("${yummy-system.text.public.parameter-error}")
    private String parameterErrorText;
    @Value("${yummy-system.text.public.user-not-exists-error}")
    private String userNotExistsErrorText;
    @Value("${yummy-system.text.public.state-error}")
    private String stateErrorText;
    @Value("${yummy-system.text.public.not-login-error}")
    private String notLoginErrorText;
    @Value("${yummy-system.text.public.telephone-duplicated}")
    private String telephoneDuplicatedText;
    @Value("${yummy-system.text.public.no-such-info-error}")
    private String noSuchInfoErrorText;

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
        model.addAttribute("modificationInfos", managementBlService.getWaitingRestaurantModificationInfo());
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
    public Map<String, Object> approve(HttpServletRequest request, @RequestParam("id") Integer id, @RequestParam("pass") Boolean pass) {
        Map<String, Object> result = new HashMap<>();
        if (request.getSession(true).getAttribute("manager") == null) {
            result.put("info", notLoginErrorText);
            return result;
        }

        try {
            managementBlService.approveRestaurant(id, pass);
            result.put("result", true);
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText);
        } catch (UnupdatableException e) {
            result.put("info", stateErrorText + ":" + e.getCurrentStateText());
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
            result.put("info", notLoginErrorText);
            return result;
        }

        try {
            managementBlService.confirmModification(modificationId, pass);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (DuplicatedUniqueKeyException e) {
            result.put("info", telephoneDuplicatedText);
        } catch (NoSuchAttributeException e) {
            result.put("info", noSuchInfoErrorText);
        }

        return result;
    }
}

