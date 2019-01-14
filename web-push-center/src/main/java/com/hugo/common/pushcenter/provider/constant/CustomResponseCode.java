package com.hugo.common.pushcenter.provider.constant;

import com.hugo.api.api.model.Code;

/**
 * @Author wangweiguang
 */
public enum CustomResponseCode implements Code {
    INVALID_CONNECT(198001, "没有建立连接通道"),
    INPUT_PARAMS_NULL(195020, "必填参数不能为空"),
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
