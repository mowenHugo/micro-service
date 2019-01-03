package com.hugo.a.controller;

import com.hugo.a.service.BHelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HelloController {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    BHelloService bHelloService;

    @GetMapping("/aServer")
    public String hi(HttpServletRequest request) {
        String traceId = request.getHeader("X-B3-TraceId");
        String spanId = request.getHeader("X-B3-SpanId");
        String parentSpanId = request.getHeader("X-B3-ParentSpanId");
        String sampled = request.getHeader("X-B3-Sampled");
        String spanName = request.getHeader("X-Span-Name");
        String userId = request.getHeader("userId"); //工作单元名称
        String userName = request.getHeader("userName"); //工作单元名称
        System.out.println("traceId: " + traceId);
        System.out.println("spanId: " + spanId);
        System.out.println("parentSpanId: " + parentSpanId);
        System.out.println("sampled: " + sampled);
        System.out.println("spanName: " + spanName);
        System.out.println("userId: " + userId);
        System.out.println("userName: " + userName);
        return bHelloService.callHi("hugo");
//        return restTemplate.getForObject("http://localhost:19992/bServer?name=" + name, String.class);
    }
}
