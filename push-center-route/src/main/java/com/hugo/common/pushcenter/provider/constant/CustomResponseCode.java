package com.hugo.common.pushcenter.provider.constant;

import com.hugo.api.api.model.Code;

/**
 * @Author wangweiguang
 */
public enum CustomResponseCode implements Code {
    INVALID_CONNECT(198001, "没有建立连接通道"),
    INPUT_PARAMS_NULL(195020, "必填参数不能为空"),
    MESSAGE_SIZE_GREATER_THAN_500(195020, "单次推送消息数量不能大于500条"),
    NORMAL_CLOSE(198100, "浏览器关闭了通道");

    private int code;
    private String message;

    private CustomResponseCode(int code, String message) {
        this.setCode(code);
        this.setMessage(message);
    }

    @Override
    public String toString() {
        return Integer.toString(getCode());
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }
    }
