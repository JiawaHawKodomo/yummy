package com.kodomo.yummy.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-18 11:22
 */
@WebFilter(urlPatterns = "/management/*", filterName = "management_filter")
public class ManagementFilter extends HttpFilter {

    @Override
    public void doFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String uri = servletRequest.getRequestURI();
        if (uri.startsWith("/management") && !uri.contains("login") && !uri.contains("register")) {
            if (servletRequest.getSession(true).getAttribute("manager") == null) {
                servletResponse.sendRedirect("/management/login");
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
