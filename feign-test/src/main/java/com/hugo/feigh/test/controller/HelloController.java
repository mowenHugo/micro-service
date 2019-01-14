package com.hugo.feigh.test.controller;

import com.hugo.api.api.model.RspData;
import com.hugo.feigh.test.service.SendMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class HelloController {

    @Autowired
    private SendMessageService sendMessageService;

    @GetMapping("/sendMessage")
    public RspData<List<String>> sendMessage(HttpServletRequest request) {
        return sendMessageService.sendMessage();
    }
}