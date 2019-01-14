package com.hugo.common.pushcenter.provider.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;
import java.util.Map;

/**
 * @Author wangweiguang
 */
public interface WebSocketSession {

    String getUserId();

    String getChannelId();

    Channel getChannel();

    void sendMessage(WebSocketFrame webSocketFrame);

    HttpHeaders getHandshakeHeaders();

    Map<String, List<String>> getParameters();

    ChannelHandlerContext getChannelHandlerContext();

}
