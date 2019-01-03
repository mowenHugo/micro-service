package com.hugo.push.controller;

import com.hugo.push.model.Message;
import com.hugo.push.service.SendSocketMessageRemoteService;
import com.hugo.push.service.SendSocketMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author wangweiguang
 */
@RestController
public class SendSocketMessageController implements SendSocketMessageRemoteService {

    private static final Logger logger = LoggerFactory.getLogger(SendSocketMessageController.class);

    @Autowired
    private SendSocketMessageService sendSocketMessageService;

    @Override
    public Integer sendSocketMessage( @RequestBody Message Message) {
        return sendSocketMessageService.sendSocketMessage(Message);
    }
}
