package com.hugo.common.pushcenter.provider.sdk.builder;

public interface PushCenterAppBuilder {

    PushCenterAppBuilder env(String var1);

    PushCenterAppBuilder from(String var1);

    PushCenterApiBuilder api(String url, String api);

    String getAppKey();

    String getAppSecret();

    String getEnv();

    String getFrom();

    String getDomain();
}
