package com.hugo.c;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
//@SpringCloudApplication已经包含@EnableDiscoveryClient 、@SpringBootApplication、@EnableCircuitBreaker注解
public class CServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CServerApplication.class, args);
    }
}
