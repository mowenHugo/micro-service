package com.hugo.common.pushcenter.provider.sdk.builder;

import com.hugo.api.api.model.PushMessage;
import com.hugo.common.pushcenter.provider.sdk.exception.PushCenterException;

import java.util.List;
import java.util.Map;

public interface PushCenterApiBuilder {

    PushCenterApiBuilder body(String body);

    PushCenterApiBuilder body(PushMessage message);

    PushCenterApiBuilder body(List<PushMessage> messages);

    PushCenterResponseBuilder request() throws PushCenterException;

    PushCenterResponseBuilder request(Map<String, String> var1) throws PushCenterException;

    String getApiCode();

    String getUrl();

    Object getBody();

    Map<String, String> getHeaders();

    PushCenterApiBuilder connectTimeout(int var1);

    PushCenterApiBuilder readTimeout(int var1);

    int getConnectTimeout();

    int getReadTimeout();

    PushCenterAppBuilder getPushCenterAppBuilder();

    void setPushCenterAppBuilder(PushCenterAppBuilder var1);
}
