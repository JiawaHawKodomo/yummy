package com.kodomo.yummy.filter;

import org.springframework.core.annotation.Order;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-17 22:17
 */
//@Order(1)
//@WebFilter(urlPatterns = "/*", filterName = "open_session_filter")
public class OpenSessionFilter implements Filter {

    private final OpenSessionInViewFilter filter;

    public OpenSessionFilter() {
        filter = new OpenSessionInViewFilter();
        filter.setSessionFactoryBeanName("sessionFactoryYummy");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filter.doFilter(servletRequest, servletResponse, filterChain);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        filter.init(filterConfig);
    }

    @Override
    public void destroy() {
        filter.destroy();
    }
}
