package com.hugo.common.pushcenter.provider.sdk.exception;

public class PushCenterException extends Exception {

    private String errCode;
    private String errMsg;

    public PushCenterException() {
    }

    public PushCenterException(String message, Throwable cause) {
        super(message, cause);
    }

    public PushCenterException(String message) {
        super(message);
    }

    public PushCenterException(Throwable cause) {
        super(cause);
    }

    public PushCenterException(String errCode, String errMsg) {
        super(errCode + ":" + errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return this.errCode;
    }

    public String getErrMsg() {
        return this.errMsg;
    }
}