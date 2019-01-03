package com.hugo.a.service;

import com.hugo.a.service.fallback.BHelloFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "b-server", fallback = BHelloFallback.class)
public interface BHelloService {

    @RequestMapping(value = "/bServer", method = RequestMethod.GET)
    String callHi(@RequestParam("name") String name);
}
