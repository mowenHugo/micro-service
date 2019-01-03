package com.hugo.push.client;

/**
 * @Author wangweiguang
 */
public class WebSocketMessage {

    private String messageType; //消息类型

    private String content; //消息内容

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
