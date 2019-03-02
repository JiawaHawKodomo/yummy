package com.kodomo.yummy.controller.customer;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * info页面的controller
 *
 * @author Shuaiyu Yao
 * @create 2019-02-20 14:47
 */
@RequestMapping("/customer")
@Controller
public class CustomerInfoController {

    @Value("${lbs.key}")
    private String lbsKey;

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
    @Value("${yummy-system.text.public.no-such-location-error}")
    private String noSuchLocationErrorText;
    @Value("${yummy-system.text.customer.old-password-error}")
    private String oldPasswordErrorText;

    private final CustomerBlService customerBlService;

    @Autowired
    public CustomerInfoController(CustomerBlService customerBlService) {
        this.customerBlService = customerBlService;
    }

    /**
     * 修改个人信息
     *
     * @param name
     * @param telephone
     * @param request
     * @return
     */
    @RequestMapping(value = "/info", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> infoUpdate(@RequestParam("name") String name
            , @RequestParam("telephone") String telephone, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        String customerEmail = (String) request.getSession(true).getAttribute("customer");
        if (customerEmail == null) {
            result.put("info", notLoginErrorText);
            return result;
        }
        try {
            customerBlService.updateCustomerInfo(customerEmail, name, telephone);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText);
        } catch (UnupdatableException e) {
            result.put("info", stateErrorText + ":" + e.getCurrentStateText());
        }

        return result;
    }

    /**
     * 修改密码
     *
     * @param oldPassword
     * @param password
     * @param request
     * @return
     */
    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> passwordUpdate(@RequestParam("oldPassword") String oldPassword,
                                              @RequestParam("password") String password, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        String customerEmail = (String) request.getSession(true).getAttribute("customer");
        if (customerEmail == null) {
            result.put("info", notLoginErrorText);
        }

        try {
            customerBlService.updateCustomerPassword(customerEmail, oldPassword, password);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText);
        } catch (UnupdatableException e) {
            result.put("info", stateErrorText + ":" + e.getCurrentStateText());
        } catch (PasswordErrorException e) {
            result.put("info", oldPasswordErrorText);
        }
        return result;
    }

    /**
     * 新建地址
     *
     * @param request
     * @return
     */
    @PostMapping("/location")
    @ResponseBody
    public Map<String, Object> locationCreate(HttpServletRequest request, @RequestParam("block") String block,
                                              @RequestParam("point") String point,
                                              @RequestParam("note") String note,
                                              @RequestParam("city") String city,
                                              @RequestParam("telephone") String telephone,
                                              @RequestParam("lat") Double lat, @RequestParam("lng") Double lng) {
        Map<String, Object> result = new HashMap<>();
        String customerEmail = (String) request.getSession(true).getAttribute("customer");
        if (customerEmail == null) {
            result.put("info", "未登录");
        }

        try {
            customerBlService.addLocationForCustomer(customerEmail, block, point, note, city, telephone, lat, lng);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText);
        } catch (UnupdatableException e) {
            result.put("info", stateErrorText + ":" + e.getCurrentStateText());
        }
        return result;
    }

    /**
     * 删除地址信息
     *
     * @param request
     * @param locationId
     * @return
     */
    @RequestMapping(value = "/location", method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String, Object> locationDelete(HttpServletRequest request, @RequestParam("locationId") Integer locationId) {
        Map<String, Object> result = new HashMap<>();
        String customerEmail = (String) request.getSession(true).getAttribute("customer");
        if (customerEmail == null) {
            result.put("info", "未登录");
        }

        try {
            customerBlService.deleteLocationForCustomer(customerEmail, locationId);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText);
        } catch (UnupdatableException e) {
            result.put("info", stateErrorText + ":" + e.getCurrentStateText());
        } catch (NoSuchAttributeException e) {
            result.put("info", noSuchLocationErrorText);
        }
        return result;
    }

    /**
     * 注销账号
     *
     * @param request  request
     * @param password password
     * @return map
     */
    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String, Object> closeCustomer(HttpServletRequest request, @RequestParam("password") String password) {
        Map<String, Object> result = new HashMap<>();
        String customerEmail = (String) request.getSession(true).getAttribute("customer");
        if (customerEmail == null) {
            result.put("info", "未登录");
        }

        try {
            customerBlService.closeCustomer(customerEmail, password);
            result.put("result", true);
            //取消登陆状态
            request.getSession(true).setAttribute("customer", null);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText);
        } catch (UnupdatableException e) {
            result.put("info", stateErrorText + ":" + e.getCurrentStateText());
        } catch (PasswordErrorException e) {
            result.put("info", passwordErrorText);
        }
        return result;
    }

}
