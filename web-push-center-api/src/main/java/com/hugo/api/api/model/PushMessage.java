package com.hugo.api.api.model;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author wangweiguang
 */
public class PushMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serviceCode;

    private String moduleName;

    @NotNull(message = "MESSAGE_TYPE_NULL")
    private String messageType; //消息类型

    @NotNull(message = "CONTENT_NULL")
    private Object content; //消息内容

    @NotNull(message = "USER_ID_NULL")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

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

    @Override
    public String toString() {
        return "PushMessage [serviceCode=" + serviceCode + ", moduleName=" + moduleName + ", messageType=" + messageType
                + ", content=" + content + ", userId=" + userId + "]";
    }

}
