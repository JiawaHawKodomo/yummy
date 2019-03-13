package com.kodomo.yummy.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
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
 * @create 2019-02-16 11:59
 */
@Order(2)
@WebFilter(urlPatterns = "/customer/*", filterName = "customer_login_filter")
public class CustomerFilter extends HttpFilter {

    private static List<String> shielded = new ArrayList<String>() {{
        add("/customer/login");
    }};

    private boolean isUnderShield(String uri) {
        return shielded.stream().map(uri::startsWith).reduce((a, b) -> a || b).orElse(false);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String url = request.getRequestURI();
        HttpSession session = request.getSession(true);
        if (url.startsWith("/customer/") && !isUnderShield(url)) {
            if (session.getAttribute("customer") == null) {
                response.sendRedirect("/?role=0");
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
