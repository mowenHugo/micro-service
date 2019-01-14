package com.hugo.common.pushcenter.provider.controller;

import com.alibaba.fastjson.JSON;
import com.hugo.api.api.model.PushMessage;
import com.hugo.api.api.model.RspData;
import com.hugo.api.api.service.SendSocketMessageRemoteService;
import com.hugo.common.pushcenter.provider.constant.CustomResponseCode;
import com.hugo.common.pushcenter.provider.service.SendSocketMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author wangweiguang
 */
@RestController
public class SendSocketMessageController implements SendSocketMessageRemoteService {

    private static final Logger logger = LoggerFactory.getLogger(SendSocketMessageController.class);

    @Autowired
    private SendSocketMessageService sendSocketMessageService;

    //@FluentValid

    /**
     * 单个消息推送
     */
    @Override
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public RspData<Integer> sendSocketMessage(@RequestBody PushMessage PushMessage) {
        logger.debug("推送消息请求参数:{}", JSON.toJSONString(PushMessage));
        return sendSocketMessageService.sendSocketMessage(PushMessage);
    }

    /**
     * 批量推送消息,目前单次调用支持最大500条
     */
    @Override
    @RequestMapping(value = "/send/list", method = RequestMethod.POST)
    public RspData<List<String>> sendSocketMessages(@RequestBody List<PushMessage> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            RspData<List<String>> responseData = new RspData<List<String>>();
            responseData.setCode(CustomResponseCode.INPUT_PARAMS_NULL.getCode());
            responseData.setMsg(CustomResponseCode.INPUT_PARAMS_NULL.getMessage());
            return responseData;
        }
        if (messages.size() > 500) {
            RspData<List<String>> responseData = new RspData<List<String>>();
            responseData.setCode(CustomResponseCode.MESSAGE_SIZE_GREATER_THAN_500.getCode());
            responseData.setMsg(CustomResponseCode.MESSAGE_SIZE_GREATER_THAN_500.getMessage());
            return responseData;
        }
        logger.debug("推送消息请求参数:{}", JSON.toJSONString(messages));
        return sendSocketMessageService.sendSocketMessages(messages);
    }
}
