package com.kodomo.yummy.controller.restaurant;

import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.controller.vo.OfferingTypeVo;
import com.kodomo.yummy.controller.vo.OfferingVo;
import com.kodomo.yummy.controller.vo.RestaurantStrategyVo;
import com.kodomo.yummy.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    private final RestaurantBlService restaurantBlService;

    @Autowired
    public RestaurantManageController(RestaurantBlService restaurantBlService) {
        this.restaurantBlService = restaurantBlService;
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
            result.put("info", "未登录");
            return result;
        }

        try {
            restaurantBlService.updateRestaurantOfferingType(rid, list);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", "输入的名称不正确");
        } catch (UserNotExistsException e) {
            result.put("info", "用户不存在");
        } catch (DuplicatedPrimaryKeyException e) {
            result.put("info", "分类名称不能重复");
        } catch (UnupdatableException e) {
            result.put("info", "该账号状态下无法修改:" + e.getCurrentStateText());
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
            result.put("info", "未登录");
            return result;
        }

        try {
            restaurantBlService.saveOffering(rid, vo);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", "以下信息填写有误:" + e.getErrorFieldsInfo());
        } catch (UserNotExistsException e) {
            result.put("info", "用户不存在");
        } catch (UnupdatableException e) {
            result.put("info", "该账号状态下无法修改:" + e.getCurrentStateText());
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
            result.put("info", "未登录");
            return result;
        }

        try {
            restaurantBlService.deleteOffering(rid, id);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", "参数不正确");
        } catch (NoSuchAttributeException | UnupdatableException e) {
            result.put("info", "不存在该餐品");
        } catch (DatabaseUnknownException e) {
            result.put("info", "数据库未知错误:" + e.getMessage());
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
            result.put("info", "未登录");
            return result;
        }

        try {
            restaurantBlService.addRestaurantStrategy(rid, vos);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", "参数不正确");
        } catch (UserNotExistsException e) {
            result.put("info", "该餐厅不存在");
        } catch (DatabaseUnknownException e) {
            result.put("info", "数据库错误:" + e.getMessage());
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
            result.put("info", "未登录");
            return result;
        }

        try {
            restaurantBlService.deleteRestaurantStrategy(rid, id);
            result.put("result", true);
        } catch (DatabaseUnknownException e) {
            result.put("info", "数据库错误:" + e.getMessage());
        } catch (NoSuchAttributeException | UnupdatableException e) {
            result.put("info", "不存在该策略, 无法删除");
        } catch (ParamErrorException e) {
            result.put("info", "参数不正确:" + e.getErrorFieldsInfo());
        }

        return result;
    }
}