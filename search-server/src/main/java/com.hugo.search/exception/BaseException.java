package com.hugo.search.exception;

import com.hugo.search.constant.BaseCode;

public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private BaseCode baseCode;

    public BaseException() {

    }

    public BaseException(BaseCode baseCode) {
        super(baseCode.getMessage());
        this.baseCode = baseCode;
    }

    /**
     * 多个参数，格式化输出
     */
    public BaseException(BaseCode baseCode, Object... msgArgs) {
        super(String.format(baseCode.getMessage(), msgArgs));
        this.baseCode = baseCode;
    }

    public BaseException(int code, String message) {
        this(code, message, null);
    }

    public BaseException(int code, String message, Throwable cause) {
        super(message, cause);
        this.baseCode = new DefaultBaseCode(code, message);
    }

    public BaseCode getAppCode() {
        return baseCode;
    }

    class DefaultBaseCode implements BaseCode {
        private int code;
        private String message;

        public DefaultBaseCode(int code, String message) {
            this.setCode(code);
            this.setMessage(message);
        }

        @Override
        public int getCode() {
            return code;
        }

        @Override
        public void setCode(int code) {
            this.code = code;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
