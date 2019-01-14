package com.hugo.common.pushcenter.provider.sdk;

import com.hugo.common.pushcenter.provider.sdk.builder.PushCenterAppBuilder;
import com.hugo.common.pushcenter.provider.sdk.builder.impl.DefaultPushCenterAppBuilder;

public class PushCenterApi {
    public PushCenterApi() {
    }

    public static PushCenterAppBuilder builder(String appkey, String appsecret) {
        return new DefaultPushCenterAppBuilder(appkey, appsecret);
    }
}
