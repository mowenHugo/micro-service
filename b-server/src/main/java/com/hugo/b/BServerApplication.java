package com.hugo.b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableEurekaClient
@SpringBootApplication
//@SpringCloudApplication已经包含@EnableDiscoveryClient 、@SpringBootApplication、@EnableCircuitBreaker注解
public class BServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BServerApplication.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
