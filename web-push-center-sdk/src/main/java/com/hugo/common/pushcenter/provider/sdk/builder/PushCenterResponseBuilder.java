package com.hugo.common.pushcenter.provider.sdk.builder;

import com.hugo.common.pushcenter.provider.sdk.exception.PushCenterException;

public interface PushCenterResponseBuilder {

    String response();

    <T> T response(Class<T> var1) throws PushCenterException;
}
