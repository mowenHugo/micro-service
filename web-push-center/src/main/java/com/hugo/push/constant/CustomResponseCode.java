package com.hugo.push.constant;

/**
 * @Author wangweiguang
 */
public enum CustomResponseCode {

    INVALID_CONNECT(98001, "没有建立连接通道"),
    NORMAL_CLOSE(98100, "浏览器关闭了通道");

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
