package com.kodomo.yummy.controller.customer;

import com.kodomo.yummy.bl.CustomerBlService;
import com.kodomo.yummy.entity.Customer;
import com.kodomo.yummy.entity.Restaurant;
import com.kodomo.yummy.exceptions.NoSuchAttributeException;
import com.kodomo.yummy.exceptions.ParamErrorException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-22 17:16
 */
@RequestMapping("/customer")
@Controller
public class CustomerPlaceController {

    private final CustomerBlService customerBlService;

    @Autowired
    public CustomerPlaceController(CustomerBlService customerBlService) {
        this.customerBlService = customerBlService;
    }

    /**
     * 选择地址
     *
     * @return
     */
    @GetMapping("/place")
    public String place(HttpServletRequest request, Model model,
                        @RequestParam(value = "location_id", required = false) Integer locationId,
                        @RequestParam(value = "search", required = false) String search) {
        String email = (String) request.getSession(true).getAttribute("customer");
        Customer customer = customerBlService.getCustomerEntityByEmail(email);
        model.addAttribute("customer", customer);
        //选择地址, 查找
        if (locationId != null) {
            try {
                List<Restaurant> restaurants = customerBlService.getRestaurantWithinDistributionDistance(email, locationId);
                model.addAttribute("resultRestaurants", restaurants);
                model.addAttribute("locationId", locationId);
                if (search != null) {
                    model.addAttribute("keyWord", search);
                }
            } catch (ParamErrorException | NoSuchAttributeException | UserNotExistsException ignored) {
            }
        }
        return "customer/place";
    }
}
