package com.hugo.common.pushcenter.provider.sdk.builder.impl;


import com.hugo.api.api.model.PushMessage;
import com.hugo.common.pushcenter.provider.sdk.PushCenterUtils;
import com.hugo.common.pushcenter.provider.sdk.builder.PushCenterApiBuilder;
import com.hugo.common.pushcenter.provider.sdk.builder.PushCenterAppBuilder;
import com.hugo.common.pushcenter.provider.sdk.builder.PushCenterResponseBuilder;
import com.hugo.common.pushcenter.provider.sdk.exception.PushCenterException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultPushCenterApiBuilder implements PushCenterApiBuilder {
    private String api;
    private Object body;
    private String url;
    private Map<String, String> headers = new HashMap();
    private PushCenterAppBuilder pushCenterAppBuilder;
    private int connectTimeout = 3000;
    private int readTimeout = 15000;

    @Override
    public PushCenterApiBuilder body(String body) {
        this.body = body;
        return this;
    }

    public PushCenterApiBuilder body(PushMessage message) {
        this.body = message;
        return this;
    }

    public DefaultPushCenterApiBuilder(PushCenterAppBuilder pushCenterAppBuilder, String url, String api) {
        this.pushCenterAppBuilder = pushCenterAppBuilder;
        this.url = url;
        this.api = api;
    }

    public PushCenterApiBuilder body(List<PushMessage> body) {
        this.body = body;
        return this;
    }

    public PushCenterApiBuilder body(Map<String, Object> body) {
        this.body = body;
        return this;
    }

    public PushCenterApiBuilder header(String header, String value) {
        this.headers.put(header, value);
        return this;
    }

    public PushCenterApiBuilder connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public PushCenterApiBuilder readTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public PushCenterResponseBuilder request() throws PushCenterException {
        return this.request((Map) null);
    }

    public PushCenterResponseBuilder request(Map<String, String> headers) throws PushCenterException {
        if (headers != null) {
            this.headers.putAll(headers);
        }
        String responseContent = PushCenterUtils.execPushCenterApiInvoke(this);
        return new DefaultPushCenterResponseBuilder(responseContent);
    }

    public String getApiCode() {
        return this.api;
    }

    public Object getBody() {
        return this.body;
    }

    public String getUrl() {
        return this.url;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public PushCenterAppBuilder getPushCenterAppBuilder() {
        return this.pushCenterAppBuilder;
    }

    public void setPushCenterAppBuilder(PushCenterAppBuilder pushCenterAppBuilder) {
        this.pushCenterAppBuilder = pushCenterAppBuilder;
    }
}
