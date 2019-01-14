package com.hugo.feigh.test.service;

import com.hugo.api.api.model.PushMessage;
import com.hugo.api.api.model.RspData;
import com.hugo.common.pushcenter.provider.client.SendSocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SendMessageService {

    @Autowired
    private SendSocketMessageService sendSocketMessageService;

    public RspData<List<String>> sendMessage() {
        List<PushMessage> messageList = new ArrayList<>();
        for (int i = 1; i <= 500; i++) {
            PushMessage message = new PushMessage();
            message.setContent("推送信息" + i);
            message.setMessageType("1");
            message.setModuleName("moduleName");
            message.setServiceCode("1111111");
            message.setUserId("123456");
            messageList.add(message);
        }
        RspData<List<String>> responseData = sendSocketMessageService.sendSocketMessages(messageList);
        return responseData;
    }
}
