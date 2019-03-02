package com.kodomo.yummy.controller.restaurant;

import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.controller.vo.OfferingTypeVo;
import com.kodomo.yummy.controller.vo.OfferingVo;
import com.kodomo.yummy.controller.vo.RestaurantStrategyVo;
import com.kodomo.yummy.entity.restaurant.Restaurant;
import com.kodomo.yummy.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-21 13:03
 */
@RequestMapping("/restaurant")
@Controller
public class RestaurantManageController {

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
    @Value("${yummy-system.text.restaurant.duplicated-type-name}")
    private String duplicatedTypeNameText;
    @Value("${yummy-system.text.restaurant.date-setting-error}")
    private String dateSettingErrorText;
    @Value("${yummy-system.text.restaurant.no-such-offering}")
    private String offeringDoesNotExistText;
    @Value("${yummy-system.text.restaurant.no-such-strategy}")
    private String strategyDoesNotExistText;

    private final RestaurantBlService restaurantBlService;

    @Autowired
    public RestaurantManageController(RestaurantBlService restaurantBlService) {
        this.restaurantBlService = restaurantBlService;
    }

    @GetMapping("/manage")
    public String manage(HttpServletRequest request, Model model) {
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        Restaurant restaurant = restaurantBlService.getRestaurantById(rid);
        model.addAttribute("restaurant", restaurant);
        return "restaurant/manage";
    }

    /**
     * 修改商品类型信息
     *
     * @param request
     * @param list
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/type", method = RequestMethod.PUT)
    public Map<String, Object> updateType(HttpServletRequest request, @RequestBody List<OfferingTypeVo> list) {
        Map<String, Object> result = new HashMap<>();
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        if (rid == null) {
            result.put("info", notLoginErrorText);
            return result;
        }

        try {
            restaurantBlService.updateRestaurantOfferingType(rid, list);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText);
        } catch (DuplicatedUniqueKeyException e) {
            result.put("info", duplicatedTypeNameText);
        } catch (UnupdatableException e) {
            result.put("info", stateErrorText + ":" + e.getCurrentStateText());
        }

        return result;
    }

    /**
     * 添加或修改商品
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/offering", method = RequestMethod.POST)
    public Map<String, Object> createOffering(HttpServletRequest request,
                                              @RequestBody OfferingVo vo) {
        Map<String, Object> result = new HashMap<>();
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        if (rid == null) {
            result.put("info", notLoginErrorText);
            return result;
        }

        try {
            restaurantBlService.saveOffering(rid, vo);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText);
        } catch (UnupdatableException e) {
            result.put("info", stateErrorText + ":" + e.getCurrentStateText());
        } catch (DateSettingException e) {
            result.put("info", dateSettingErrorText);
        }
        return result;
    }

    /**
     * 删除商品
     *
     * @param request
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/offering", method = RequestMethod.DELETE)
    public Map<String, Object> deleteOffering(HttpServletRequest request, @RequestParam("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        if (rid == null) {
            result.put("info", notLoginErrorText);
            return result;
        }

        try {
            restaurantBlService.deleteOffering(rid, id);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (NoSuchAttributeException | UnupdatableException e) {
            result.put("info", offeringDoesNotExistText);
        }
        return result;
    }

    /**
     * 添加策略
     *
     * @param request
     * @param vos
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/strategy", method = RequestMethod.POST)
    public Map<String, Object> addRestaurantStrategy(HttpServletRequest request,
                                                     @RequestBody List<RestaurantStrategyVo> vos) {
        Map<String, Object> result = new HashMap<>();
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        if (rid == null) {
            result.put("info", notLoginErrorText);
            return result;
        }

        try {
            restaurantBlService.addRestaurantStrategy(rid, vos);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText);
        }
        return result;
    }

    /**
     * 删除策略
     *
     * @param request
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/strategy", method = RequestMethod.DELETE)
    public Map<String, Object> deleteRestaurantStrategy(HttpServletRequest request,
                                                        @RequestParam("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");
        if (rid == null) {
            result.put("info", notLoginErrorText);
            return result;
        }

        try {
            restaurantBlService.deleteRestaurantStrategy(rid, id);
            result.put("result", true);
        } catch (NoSuchAttributeException | UnupdatableException e) {
            result.put("info", strategyDoesNotExistText);
        } catch (ParamErrorException e) {
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        }

        return result;
    }
}
