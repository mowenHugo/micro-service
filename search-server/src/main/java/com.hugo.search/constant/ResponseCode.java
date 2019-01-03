package com.hugo.search.constant;

public enum  ResponseCode implements BaseCode {

    BULK_INDEX_HAS_FAILURES(19999, "批量操作索引存在失败请求"),
    SEARCH_ERROR(20000, "查询异常");

    private int code;
    private String message;

    private ResponseCode(int code, String message) {
        this.setCode(code);
        this.setMessage(message);
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }
}
