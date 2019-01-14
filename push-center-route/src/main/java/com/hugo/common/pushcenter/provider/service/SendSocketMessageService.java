package com.hugo.common.pushcenter.provider.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hugo.api.api.model.PushMessage;
import com.hugo.api.api.model.RspData;
import com.hugo.common.pushcenter.provider.constant.CustomResponseCode;
import com.hugo.common.pushcenter.provider.sdk.PushCenterApi;
import com.hugo.common.pushcenter.provider.sdk.builder.PushCenterApiBuilder;
import com.hugo.common.pushcenter.provider.sdk.exception.PushCenterException;
import com.hugo.common.pushcenter.provider.util.ChannelUtil;
import com.hugo.common.pushcenter.provider.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * @Author wangweiguang
 */
@Service
public class SendSocketMessageService {

    @Autowired
    private RedisUtil redisUtil;
    @Value("${spring.profiles.active}")
    private String env;


    public RspData<Integer> sendSocketMessage(PushMessage message) {
        RspData<Integer> responseData = new RspData<Integer>();
        Set<String> userUserConnectionServerInfo = getServerIPsByUserId(message.getUserId());
        if (CollectionUtils.isEmpty(userUserConnectionServerInfo)) {
            //todo 完善通道找不到，message记录到DB
            responseData.setCode(CustomResponseCode.INVALID_CONNECT.getCode());
            responseData.setMsg(CustomResponseCode.INVALID_CONNECT.getMessage());
            return responseData;
        }
        try {
            doRequest(userUserConnectionServerInfo, message);
        } catch (PushCenterException e) {
            e.printStackTrace();
        }
        responseData.ok();
        return responseData;
    }

    public RspData<List<String>> sendSocketMessages(List<PushMessage> messages) {
        RspData<List<String>> responseData = new RspData<>();
        ChannelUtil.executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                //serverIP,sendMessages
                Map<String, List<PushMessage>> sendMessageMap = Maps.newHashMap();
                messages.forEach(message -> {
                    Set<String> userUserConnectionServerIP = getServerIPsByUserId(message.getUserId());
                    if (CollectionUtils.isEmpty(userUserConnectionServerIP)) {
                        return;
                    }
                    userUserConnectionServerIP.forEach(serverIP -> {
                        List<PushMessage> sendMessages = sendMessageMap.get(serverIP);
                        if (sendMessages == null) {
                            sendMessages = new ArrayList<>();
                        }
                        sendMessages.add(message);
                        sendMessageMap.put(serverIP, sendMessages);
                    });
                });
                doRequest(sendMessageMap);
                return null;
            }
        });
        responseData.ok();
        return responseData;
    }

    private Set<String> getServerIPsByUserId(String userId) {
        List<String> userUserConnectionServerInfo = ChannelUtil.getUserConnectionServerInfo(redisUtil, userId);
        if (CollectionUtils.isEmpty(userUserConnectionServerInfo)) {
            return null;
        }
        Set<String> serverIPs = Sets.newHashSet();
        for (String connectionServerInfo : userUserConnectionServerInfo) {
            serverIPs.add(connectionServerInfo.split(ChannelUtil.SERVER_IP_AND_CHANNEL_ID_SEPARATOR)[0]);
        }
        return serverIPs;
    }

    private Map doRequest(Set<String> userUserConnectionServerIP, PushMessage message) throws PushCenterException {
        try {
            for (String serverIP : userUserConnectionServerIP) {
                Map resultMap = executeRequest(serverIP, Lists.newArrayList(message));
                return resultMap;
            }
        } catch (Exception e) {
            throw new PushCenterException(e.getMessage(), e);
        }
        return null;
    }

    private void doRequest(Map<String, List<PushMessage>> sendMessageMap) throws PushCenterException {
        try {
            for (String serverIP : sendMessageMap.keySet()) {
                executeRequest(serverIP, sendMessageMap.get(serverIP));
            }
        } catch (Exception e) {
            throw new PushCenterException(e.getMessage(), e);
        }
    }

    private Map executeRequest(String serverIP, List<PushMessage> messages) throws PushCenterException {
        PushMessage message = null;
        if (messages.size() == 1) {
            message = messages.get(0);
        }

        try {
            Map resultMap = new HashMap();
            PushCenterApiBuilder apiBuilder = PushCenterApi.builder(
                    "AppKey", "AppSecret")
                    //设置环境 包含（test，dev，uat，str，prod）
                    .env(env)
                    .api(serverIP, "Api");

            //使用业务参数设置body并请求外部API
            if (message != null) {
                resultMap = apiBuilder.body(message).request().response(Map.class);
            } else {
                resultMap = apiBuilder.body(messages).request().response(Map.class);
            }
            System.out.println(JSON.toJSONString(resultMap));
            return resultMap;
        } catch (Exception e) {
            throw new PushCenterException(e.getMessage(), e);
        }
    }
}
