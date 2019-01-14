package com.hugo.common.pushcenter.provider.sdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hugo.common.pushcenter.provider.sdk.builder.PushCenterApiBuilder;
import com.hugo.common.pushcenter.provider.sdk.builder.PushCenterAppBuilder;
import com.hugo.common.pushcenter.provider.sdk.exception.PushCenterException;

import java.io.IOException;

public class PushCenterUtils {

    public PushCenterUtils() {
    }

    public static String execPushCenterApiInvoke(PushCenterApiBuilder pushCenterApiBuilder) throws PushCenterException {
        PushCenterAppBuilder pushCenterAppBuilder = pushCenterApiBuilder.getPushCenterAppBuilder();
        if (StringUtils.isEmpty(pushCenterAppBuilder.getAppKey())) {
            throw new PushCenterException("appKey is null");
        }
        if (StringUtils.isEmpty(pushCenterAppBuilder.getAppSecret())) {
            throw new PushCenterException("appSecret is null");
        }
        if (StringUtils.isEmpty(pushCenterApiBuilder.getApiCode())) {
            throw new PushCenterException("apiCode is null");
        }
        if (StringUtils.isEmpty(pushCenterApiBuilder.getUrl())) {
            throw new PushCenterException("url is null");
        }
        if (pushCenterApiBuilder.getBody() != null) {
            try {
                if (String.class.isAssignableFrom(pushCenterApiBuilder.getBody().getClass())) {
                    pushCenterApiBuilder.body(pushCenterApiBuilder.getBody().toString());
                } else {
                    pushCenterApiBuilder.body(JsonUtils.deserializer(pushCenterApiBuilder.getBody()));
                }
            } catch (JsonProcessingException var3) {
                throw new PushCenterException("JSON dserialize error", var3);
            }
        }
        RequestHolder requestHolder = getRestRequestHolder(pushCenterApiBuilder);
        return executeRest(requestHolder, pushCenterApiBuilder);
    }

    private static RequestHolder getRestRequestHolder(PushCenterApiBuilder pushCenterApiBuilder) throws PushCenterException {
        RequestHolder requestHolder = new RequestHolder();
        String paramJson = pushCenterApiBuilder.getBody() != null ? pushCenterApiBuilder.getBody().toString() : "{}";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String sign = getSign(pushCenterApiBuilder.getPushCenterAppBuilder().getAppSecret(), timestamp, paramJson);
        HugoHashMap headerMap = new HugoHashMap();
        headerMap.put("Content-Type", "application/json;charset=UTF-8");
        headerMap.put("appkey", pushCenterApiBuilder.getPushCenterAppBuilder().getAppKey());
        headerMap.put("sign", sign);
        headerMap.put("format", "json");
        headerMap.put("timestamp", timestamp);
        headerMap.put("method", pushCenterApiBuilder.getApiCode());
        if (pushCenterApiBuilder.getHeaders() != null) {
            headerMap.putAll(pushCenterApiBuilder.getHeaders());
        }
        requestHolder.setBodyContent(paramJson);
        requestHolder.setHeaderMap(headerMap);
        return requestHolder;
    }

    private static String executeRest(RequestHolder requestHolder, PushCenterApiBuilder pushCenterApiBuilder) throws PushCenterException {
        String rspResult = null;
        try {
            String restUrl = "http://" + pushCenterApiBuilder.getUrl() + ":8058/send/list";
            System.out.println("restUrl: " + restUrl);
            rspResult = HttpUtils.doPost(restUrl, requestHolder, pushCenterApiBuilder.getConnectTimeout(), pushCenterApiBuilder.getReadTimeout());
            return rspResult;
        } catch (IOException var4) {
            var4.printStackTrace();
            throw new PushCenterException(var4.getMessage(), var4);
        }
    }


    public static String getSign(String appSecret, String timestamp, String appParam) throws PushCenterException {
        try {
            StringBuilder content = new StringBuilder();
            content.append(appSecret).append(timestamp).append(appParam);
            return MD5Utils.md5(content.toString(), "UTF-8", "").toUpperCase();
        } catch (Exception var4) {
            throw new PushCenterException("Signature failure", var4);
        }
    }
}
