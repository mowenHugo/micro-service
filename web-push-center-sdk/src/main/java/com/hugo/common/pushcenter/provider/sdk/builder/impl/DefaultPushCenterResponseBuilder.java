package com.hugo.common.pushcenter.provider.sdk.builder.impl;

import com.hugo.common.pushcenter.provider.sdk.JsonUtils;
import com.hugo.common.pushcenter.provider.sdk.builder.PushCenterResponseBuilder;
import com.hugo.common.pushcenter.provider.sdk.exception.PushCenterException;

import java.io.IOException;

public class DefaultPushCenterResponseBuilder implements PushCenterResponseBuilder {
    private String responseContent;

    public DefaultPushCenterResponseBuilder(String responseContent) {
        this.responseContent = responseContent;
    }

    public String response() {
        return this.responseContent;
    }

    public <T> T response(Class<T> clazz) throws PushCenterException {
        if (String.class == clazz) {
            return (T) this.responseContent;
        } else {
            try {
                return JsonUtils.serializable(this.responseContent, clazz);
            } catch (IOException var3) {
                throw new PushCenterException("", var3);
            }
        }
    }
}