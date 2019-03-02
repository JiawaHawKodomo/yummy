package com.kodomo.yummy.controller.restaurant;

import com.kodomo.yummy.bl.RestaurantBlService;
import com.kodomo.yummy.controller.vo.RestaurantModificationVo;
import com.kodomo.yummy.exceptions.DuplicatedSubmitException;
import com.kodomo.yummy.exceptions.DuplicatedUniqueKeyException;
import com.kodomo.yummy.exceptions.ParamErrorException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
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
 * @create 2019-02-28 16:27
 */
@Controller
@RequestMapping("/restaurant")
public class RestaurantInfoController {

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
    @Value("${yummy-system.text.restaurant.duplicated-modification-submit}")
    private String modificationSubmitErrorText;
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
            result.put("info", parameterErrorText + e.getErrorFieldsInfo());
        } catch (DuplicatedUniqueKeyException e) {
            result.put("info", telephoneDuplicatedText);
        } catch (UserNotExistsException e) {
            result.put("info", userNotExistsErrorText);
        } catch (DuplicatedSubmitException e) {
            result.put("info", modificationSubmitErrorText);
        }
        return result;
    }
}
