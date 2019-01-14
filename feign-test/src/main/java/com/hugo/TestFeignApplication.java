package com.hugo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
//@SpringCloudApplication已经包含@EnableDiscoveryClient 、@SpringBootApplication、@EnableCircuitBreaker注解
public class TestFeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestFeignApplication.class, args);
    }

}
