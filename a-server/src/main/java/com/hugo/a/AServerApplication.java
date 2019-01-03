package com.hugo.a;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
//@SpringCloudApplication已经包含@EnableDiscoveryClient 、@SpringBootApplication、@EnableCircuitBreaker注解
public class AServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AServerApplication.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
