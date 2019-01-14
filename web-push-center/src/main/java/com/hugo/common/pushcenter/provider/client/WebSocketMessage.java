package com.hugo.common.pushcenter.provider.client;

/**
 * @Author wangweiguang
 */
public class WebSocketMessage {

    private String messageType; //消息类型

    private Object content; //消息内容

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

    
}
