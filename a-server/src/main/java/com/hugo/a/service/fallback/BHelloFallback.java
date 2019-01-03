package com.hugo.a.service.fallback;

import com.hugo.a.service.BHelloService;
import org.springframework.stereotype.Component;

@Component
public class BHelloFallback implements BHelloService {

    @Override
    public String callHi(String name) {
        return "A Server invoke B Server callHi() : Service Unavailable";
    }
}
