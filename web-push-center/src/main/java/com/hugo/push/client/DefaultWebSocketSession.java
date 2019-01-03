package com.hugo.push.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;
import java.util.Map;

/**
 * @Author wangweiguang
 */
public class DefaultWebSocketSession implements WebSocketSession {

    private String channelId;
    private String userId;
    private Channel channel;
    private HttpHeaders handshakeHeaders;
    private Map<String, List<String>> parameters;
    private ChannelHandlerContext channelHandlerContext;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getChannelId() {
        return this.channelId;
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public void sendMessage(WebSocketFrame webSocketFrame) {
        this.channelHandlerContext.writeAndFlush(webSocketFrame);
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public HttpHeaders getHandshakeHeaders() {
        return this.handshakeHeaders;
    }

    @Override
    public Map<String, List<String>> getParameters() {
        return this.parameters;
    }

    @Override
    public ChannelHandlerContext getChannelHandlerContext() {
        return this.channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setHandshakeHeaders(HttpHeaders handshakeHeaders) {
        this.handshakeHeaders = handshakeHeaders;
    }

    public void setParameters(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }


}
