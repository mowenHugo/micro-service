package com.hugo.b.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class.getName());

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/bServer", method = RequestMethod.GET)
    public String callHi(HttpServletRequest request,@RequestParam("name") String name) {
        String traceId = request.getHeader("X-B3-TraceId"); //对应TraceID
        String spanId = request.getHeader("X-B3-SpanId"); //对应SpanID
        String parentSpanId = request.getHeader("X-B3-ParentSpanId"); //前面一环的SpanID
        String sampled = request.getHeader("X-B3-Sampled"); //是否被选中抽样输出
        String spanName = request.getHeader("X-Span-Name"); //工作单元名称
        String userId = request.getHeader("userId"); //工作单元名称
        String userName = request.getHeader("userName"); //工作单元名称
        System.out.println("traceId: " + traceId);
        System.out.println("spanId: " + spanId);
        System.out.println("parentSpanId: " + parentSpanId);
        System.out.println("sampled: " + sampled);
        System.out.println("spanName: " + spanName);
        System.out.println("userId: " + userId);
        System.out.println("userName: " + userName);

        logger.info("calling trace b-service");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "i'm service-b111111";
//        return restTemplate.getForObject("http://localhost:19993/cServer?name=" + name, String.class);
    }
}
