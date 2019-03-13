package com.kodomo.yummy.filter;

import org.springframework.core.annotation.Order;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-18 0:23
 */
@Order(4)
@WebFilter(urlPatterns = "/restaurant/*", filterName = "restaurant_filter")
public class RestaurantFilter extends HttpFilter {

    private static List<String> shielded = new ArrayList<String>() {{
        add("/restaurant/login");
        add("/restaurant/register");
        add("/restaurant/index");
    }};

    private boolean isUnderShield(String uri) {
        return shielded.stream().map(uri::startsWith).reduce((a, b) -> a || b).orElse(false);
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpSession session = request.getSession(true);
        String uri = request.getRequestURI();
        if (uri.startsWith("/restaurant") && !isUnderShield(uri)) {
            if (session.getAttribute("restaurant") == null) {
                response.sendRedirect("/?role=1");
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
