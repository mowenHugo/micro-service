package com.hugo.api.api.model;

public interface Code {
    int getCode();

    void setCode(int code);

    String getMessage();

    void setMessage(String message);

    default boolean isSuccess() {
        return this.getCode() == 0;
    }
}
