package com.kodomo.yummy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;

@SpringBootApplication
@ServletComponentScan
public class YummyApplication {

    public static void main(String[] args) {
        SpringApplication.run(YummyApplication.class, args);
    }

    @Bean
    public OpenSessionInViewInterceptor openSessionInViewInterceptor() {
        OpenSessionInViewInterceptor interceptor = new OpenSessionInViewInterceptor();
        return interceptor;
    }
}

