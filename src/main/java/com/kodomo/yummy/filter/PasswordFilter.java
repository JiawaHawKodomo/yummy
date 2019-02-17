package com.kodomo.yummy.filter;


import com.kodomo.yummy.filter.tool.ParameterChangableRequest;
import com.kodomo.yummy.util.Utility;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-16 15:33
 */
@Order(3)
@WebFilter(urlPatterns = {"/*"})
public class PasswordFilter extends HttpFilter {

    @Override
    public void doFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String password = servletRequest.getParameter("password");
        if (password != null) {
            //有密码, 自动转换为SHA-256加密
            ParameterChangableRequest newRequest = new ParameterChangableRequest(servletRequest);
            newRequest.addParameter("password", Utility.getSHA256Str(password));
            filterChain.doFilter(newRequest, servletResponse);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
