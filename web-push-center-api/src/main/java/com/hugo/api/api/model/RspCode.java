package com.hugo.api.api.model;

public enum RspCode implements Code {

    SUCCESS(0, "SUCCESS"),
    UNKOWN_EXCEPTION(-1, "系统压力山大,请稍后重试！");

    private int code;
    private String message;

    private RspCode(int code, String message) {
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
