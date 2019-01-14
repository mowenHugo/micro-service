package com.hugo.api.api.model;

import static com.hugo.api.api.model.RspCode.UNKOWN_EXCEPTION;

public class RspData<T> {

    private int code;
    private String msg;
    private T data;
    private String traceId;

    public RspData() {
        this(UNKOWN_EXCEPTION);
    }

    public RspData(int code, String message) {
        this.code = code;
        this.msg = message;
    }

    public RspData(int code, String message, T data) {
        this.code = code;
        this.msg = message;
        this.data = data;
    }

    public RspData(Code appCode) {
        this.code = appCode.getCode();
        this.msg = appCode.getMessage();
    }

    public RspData(Code appCode, T data) {
        this.code = appCode.getCode();
        this.msg = appCode.getMessage();
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }

    public T getData() {
        return data;
    }

    public RspData setData(T data) {
        this.data = data;
        return this;
    }

    public void responseCode(int code, String message) {
        this.code = code;
        this.msg = message;
    }

    public void ok() {
        this.code = RspCode.SUCCESS.getCode();
        this.msg = RspCode.SUCCESS.getMessage();
    }

    public boolean isSuccess() {
        return this.code == RspCode.SUCCESS.getCode();
    }
}
