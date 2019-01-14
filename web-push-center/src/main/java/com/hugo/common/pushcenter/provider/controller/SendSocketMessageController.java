package com.hugo.common.pushcenter.provider.controller;

import com.alibaba.fastjson.JSON;
import com.hugo.api.api.model.PushMessage;
import com.hugo.api.api.model.RspData;
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
public class SendSocketMessageController {

    private static final Logger logger = LoggerFactory.getLogger(SendSocketMessageController.class);

    @Autowired
    private SendSocketMessageService sendSocketMessageService;

//    @FluentValid

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public RspData<Integer> sendSocketMessage(@RequestBody PushMessage PushMessage) {
        logger.debug("推送消息请求参数:{}", JSON.toJSONString(PushMessage));
        return sendSocketMessageService.sendSocketMessage(PushMessage);
    }

    @RequestMapping(value = "/send/list", method = RequestMethod.POST)
    public RspData<List<String>> sendSocketMessages(@RequestBody List<PushMessage> Messages) {
        if (CollectionUtils.isEmpty(Messages)) {
            RspData<List<String>> RspData = new RspData<List<String>>();
            RspData.setCode(CustomResponseCode.INPUT_PARAMS_NULL.getCode());
            RspData.setMsg(CustomResponseCode.INPUT_PARAMS_NULL.getMessage());
            return RspData;
        }
        logger.debug("推送消息请求参数:{}", JSON.toJSONString(Messages));
        return sendSocketMessageService.sendSocketMessages(Messages);
    }
}
