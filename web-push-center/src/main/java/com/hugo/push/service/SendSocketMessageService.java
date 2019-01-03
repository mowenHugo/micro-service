package com.hugo.push.service;

import com.alibaba.fastjson.JSON;
import com.hugo.push.client.WebSocketMessage;
import com.hugo.push.client.WebSocketSession;
import com.hugo.push.model.Message;
import com.hugo.push.server.WebSocketServer;
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
    private WebSocketServer webSocketServer;

    public Integer sendSocketMessage(Message message) {
        List<String> channelList = webSocketServer.connectChannelMaps.get(message.getUserId());
        if (CollectionUtils.isEmpty(channelList)) {
            return 400;
        }

        for (String channelId : channelList) {
            WebSocketSession webSocketSession = webSocketServer.connectSessionMaps.get(channelId);
            if (webSocketSession == null) {
                continue;
            }
            if (!webSocketSession.getChannel().isActive() || !webSocketSession.getChannel().isWritable()) {
                webSocketServer.connectSessionMaps.remove(channelId);
                continue;
            }
            WebSocketMessage webSocketMessage = new WebSocketMessage();
            webSocketMessage.setContent(message.getContent());
            webSocketMessage.setMessageType(message.getMessageType());
            webSocketSession.getChannel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(webSocketMessage)));
        }
        return 200;
    }
}
