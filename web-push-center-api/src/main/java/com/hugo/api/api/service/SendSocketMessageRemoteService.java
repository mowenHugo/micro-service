package com.hugo.api.api.service;

import com.hugo.api.api.model.PushMessage;
import com.hugo.api.api.model.RspData;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @Author wangweiguang
 */
public interface SendSocketMessageRemoteService {

    /**
     * 单个消息推送
     */
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    RspData<Integer> sendSocketMessage(@RequestBody PushMessage PushMessage);

    /**
     * 批量推送消息,目前单次调用最大支持500条
     */
    @RequestMapping(value = "/send/list", method = RequestMethod.POST)
    RspData<List<String>> sendSocketMessages(@RequestBody List<PushMessage> pushMessages);

}
