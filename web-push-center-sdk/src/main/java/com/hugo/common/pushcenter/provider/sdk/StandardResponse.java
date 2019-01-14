package com.hugo.common.pushcenter.provider.sdk;

import java.io.Serializable;

public class StandardResponse<T> implements Serializable {
    private Integer code;
    private String msg;
    private Boolean success;
    private T data;

    public StandardResponse() {
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
