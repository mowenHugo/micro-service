package com.hugo.edge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
@EnableFeignClients
@RefreshScope

/**
 * @RefreshScope 这个注解是当application.yml配置文件发生变化的时候，不需要手动的进行重启，
 * 调用url:port/refresh,就会加载新的配置文件，当然正在访问的客户并不影响还是使用旧的配置文件，
 * 因为不是重启，后来的用户会使用新的配置文件。注意这块的刷新要用post请求。
 */
public class ZuulApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ZuulApplication.class);

    public static void main(String[] args) {
        new SpringApplicationBuilder(ZuulApplication.class).run(args);
    }

    /**
     * Web开发经常会遇到跨域问题，解决方案有：jsonp，iframe,CORS等等
     * CORS 与 JSONP 相比 :
     * 1、 JSONP只能实现GET请求，而CORS支持所有类型的HTTP请求。
     * 2、 使用CORS，开发者可以使用普通的XMLHttpRequest发起请求和获得数据，比起JSONP有更好的错误处理。
     * 3、 JSONP主要被老的浏览器支持，它们往往不支持CORS，而绝大多数现代浏览器都已经支持了CORS
     * <p>
     * 两种配置方法：
     * 1、方法级细粒度配置
     * 用@CrossOrigin(origins = "http://localhost:8080") 作用于某个Controller类上的某个方法，表示该方法支持CORS。
     * 2、全局配置
     * 以下是对 CORS 的支持全局配置：
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
            }
        };
    }

/*    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        TokenFilter httpBasicFilter = new TokenFilter();
        registrationBean.setFilter(httpBasicFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Integer.MIN_VALUE);
        return registrationBean;
    }*/
}