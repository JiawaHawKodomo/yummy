package com.kodomo.yummy.controller.restaurant;

import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.controller.vo.RestaurantModificationVo;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.exceptions.DuplicatedSubmitException;
import com.kodomo.yummy.exceptions.DuplicatedUniqueKeyException;
import com.kodomo.yummy.exceptions.ParamErrorException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-28 16:27
 */
@Controller
@RequestMapping("/restaurant")
public class RestaurantInfoController {

    private final RestaurantBlService restaurantBlService;

    @Autowired
    public RestaurantInfoController(RestaurantBlService restaurantBlService) {
        this.restaurantBlService = restaurantBlService;
    }

    @GetMapping("/info")
    public String restaurantInfo(HttpServletRequest request, Model model) {
        model.addAttribute("restaurant", restaurantBlService.getRestaurantById((Integer) request.getSession(true).getAttribute("restaurant")));
        return "restaurant/selfInfo";
    }

    @PutMapping("/info")
    @ResponseBody
    public Map<String, Object> modifyInfo(HttpServletRequest request,
                                          @RequestBody RestaurantModificationVo vo) {
        Map<String, Object> result = new HashMap<>();
        Integer rid = (Integer) request.getSession(true).getAttribute("restaurant");

        try {
            restaurantBlService.submitModification(vo, rid);
            result.put("result", true);
        } catch (ParamErrorException e) {
            result.put("info", "参数错误:" + e.getErrorFieldsInfo());
        } catch (DuplicatedUniqueKeyException e) {
            result.put("info", "该电话已经被注册");
        } catch (UserNotExistsException e) {
            result.put("info", "用户不存在");
        } catch (DuplicatedSubmitException e) {
            result.put("info", "已经提交过申请, 无法重复提交");
        }
        return result;
    }
}
