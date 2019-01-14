package com.hugo.common.pushcenter.provider.sdk.builder.impl;


import com.hugo.common.pushcenter.provider.sdk.builder.PushCenterApiBuilder;
import com.hugo.common.pushcenter.provider.sdk.builder.PushCenterAppBuilder;

import java.util.HashMap;
import java.util.Map;

public class DefaultPushCenterAppBuilder implements PushCenterAppBuilder {
    public static Map<String, String> urlMap = new HashMap();
    private String appKey;
    private String appSecret;
    private String env = "prod";
    private String from;

    public DefaultPushCenterAppBuilder(String appkey, String appsecret) {
        this.appKey = appkey;
        this.appSecret = appsecret;
    }

    public PushCenterAppBuilder env(String env) {
        this.env = env;
        return this;
    }

    public PushCenterAppBuilder from(String from) {
        this.from = from;
        return this;
    }

    @Override
    public PushCenterApiBuilder api(String url, String api) {
        return new DefaultPushCenterApiBuilder(this, url, api);
    }

    public String getAppKey() {
        return this.appKey;
    }

    public String getAppSecret() {
        return this.appSecret;
    }

    public String getEnv() {
        return this.env;
    }

    public String getFrom() {
        return this.from;
    }

    public String getDomain() {
        return (String) urlMap.get(this.env);
    }

    static {
        urlMap.put("prod", "http://www.hugo.com");
    }
}