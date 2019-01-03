package com.hugo.push.service;

import com.hugo.push.model.Message;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author wangweiguang
 */
public interface SendSocketMessageRemoteService {

    /**
     * 推送消息
     */
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    Integer sendSocketMessage(@RequestBody Message Message);

}
