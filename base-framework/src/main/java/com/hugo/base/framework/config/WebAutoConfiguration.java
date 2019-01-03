package com.hugo.base.framework.config;

import com.hugo.base.framework.filter.CustomTraceFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.instrument.web.TraceFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebAutoConfiguration {

    @Bean
    public FilterRegistrationBean indexFilterRegistration(Tracer tracer) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CustomTraceFilter(tracer));
        registration.addUrlPatterns("/*");
        registration.setOrder(TraceFilter.ORDER + 1);
        return registration;
    }
}
