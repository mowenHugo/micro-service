package com.hugo.c.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class.getName());

    @RequestMapping("/cServer")
    public String callHi(@RequestParam String name) {
        logger.info("calling trace c-service");
        return "c server hi" + name;
    }
}
