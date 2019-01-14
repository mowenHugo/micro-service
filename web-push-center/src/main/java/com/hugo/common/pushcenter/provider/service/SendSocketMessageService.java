package com.hugo.common.pushcenter.provider.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.hugo.api.api.model.PushMessage;
import com.hugo.api.api.model.RspData;
import com.hugo.common.pushcenter.provider.client.WebSocketMessage;
import com.hugo.common.pushcenter.provider.client.WebSocketSession;
import com.hugo.common.pushcenter.provider.constant.CustomResponseCode;
import com.hugo.common.pushcenter.provider.server.WebSocketServer;
import com.hugo.common.pushcenter.provider.util.RedisUtil;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author wangweiguang
 */
@Service
public class SendSocketMessageService {

    @Autowired
    private RedisUtil redisUtil;


    public RspData<Integer> sendSocketMessage(PushMessage PushMessage) {
        RspData<Integer> RspData = new RspData<Integer>();
        List<String> channelIds = WebSocketServer.connectChannelMaps.get(PushMessage.getUserId());
        if (CollectionUtils.isEmpty(channelIds)) {
            RspData.setCode(CustomResponseCode.INVALID_CONNECT.getCode());
            RspData.setMsg(CustomResponseCode.INVALID_CONNECT.getMessage());
            return RspData;
        }
        writeMessageToChannel(channelIds, PushMessage);
        RspData.ok();
        return RspData;
    }


    public RspData<List<String>> sendSocketMessages(List<PushMessage> messages) {
        RspData<List<String>> RspData = new RspData<>();
        List<String> failUserIds = Lists.newArrayList();
        messages.forEach(PushMessage -> {
            List<String> channelIds = WebSocketServer.connectChannelMaps.get(PushMessage.getUserId());
            if (CollectionUtils.isEmpty(channelIds)) {
                failUserIds.add(PushMessage.getUserId());
                return;
            }
            writeMessageToChannel(channelIds, PushMessage);
        });
        RspData.setData(failUserIds);
        RspData.ok();
        return RspData;
    }


    private void writeMessageToChannel(List<String> channelIds, PushMessage PushMessage) {
        if (CollectionUtils.isEmpty(channelIds) || PushMessage == null) {
            return;
        }
        for (String channelId : channelIds) {
            WebSocketSession webSocketSession = WebSocketServer.connectSessionMaps.get(channelId);
            if (webSocketSession == null) {
                continue;
            }
            if (!webSocketSession.getChannel().isActive() || !webSocketSession.getChannel().isWritable()) {
                WebSocketServer.connectSessionMaps.remove(channelId);
                continue;
            }
            WebSocketMessage webSocketMessage = new WebSocketMessage();
            webSocketMessage.setContent(PushMessage.getContent());
            webSocketMessage.setMessageType(PushMessage.getMessageType());
            webSocketSession.getChannel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(webSocketMessage)));
        }
    }
}
