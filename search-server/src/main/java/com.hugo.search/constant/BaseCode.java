package com.hugo.search.constant;

public interface BaseCode {

    default boolean isSuccess() {
        return this.getCode() == 200;
    }

    int getCode();

    void setCode(int code);

    String getMessage();

    void setMessage(String message);
}
