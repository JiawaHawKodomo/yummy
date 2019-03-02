package com.kodomo.yummy.controller.management;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.bl.OrderBlService;
import com.kodomo.yummy.controller.vo.CustomerLevelStrategyVo;
import com.kodomo.yummy.controller.vo.OrderRefundStrategyVo;
import com.kodomo.yummy.controller.vo.OrderSettlementStrategyVo;
import com.kodomo.yummy.exceptions.ParamErrorException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-27 10:58
 */
@Controller
@RequestMapping("/management")
public class ManagementStrategyController {

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

    private final OrderBlService orderBlService;
    private final CustomerBlService customerBlService;

    @Autowired
    public ManagementStrategyController(OrderBlService orderBlService, CustomerBlService customerBlService) {
        this.orderBlService = orderBlService;
        this.customerBlService = customerBlService;
    }

    @PostMapping("/orderStrategy")
    @ResponseBody
    public Map<String, Object> updateOrderStrategy(HttpServletRequest request,
                                                   @RequestBody List<OrderSettlementStrategyVo> vos) {
        Map<String, Object> result = new HashMap<>();
        String managerId = (String) request.getSession(true).getAttribute("manager");
        if (managerId == null) {
            result.put("info", notLoginErrorText);
            return result;
        }

        try {
            orderBlService.saveNewOrderSettlementStrategy(vos, managerId);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText);
        }
        return result;
    }

    @PostMapping("/orderRefundStrategy")
    @ResponseBody
    public Map<String, Object> updateOrderRefundStrategy(HttpServletRequest request,
                                                         @RequestBody List<OrderRefundStrategyVo> vos) {
        Map<String, Object> result = new HashMap<>();
        String managerId = (String) request.getSession(true).getAttribute("manager");
        if (managerId == null) {
            result.put("info", notLoginErrorText);
            return result;
        }

        try {
            orderBlService.saveNewOrderRefundStrategy(vos, managerId);
            result.put("result", true);
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        }
        return result;
    }

    @PostMapping("/customerLevelStrategy")
    @ResponseBody
    public Map<String, Object> updateCustomerLevelStrategy(HttpServletRequest request,
                                                           @RequestBody List<CustomerLevelStrategyVo> vos) {
        Map<String, Object> result = new HashMap<>();
        String managerId = (String) request.getSession(true).getAttribute("manager");
        if (managerId == null) {
            result.put("info", notLoginErrorText);
            return result;
        }

        try {
            customerBlService.saveCustomerLevelStrategy(vos, managerId);
            result.put("result", true);
        } catch (UserNotExistsException e) {
            result.put("info", notLoginErrorText);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        }
        return result;
    }
}
