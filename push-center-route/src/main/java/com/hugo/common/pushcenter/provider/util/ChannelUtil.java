package com.hugo.common.pushcenter.provider.util;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author wangweiguang
 */
public class ChannelUtil {

    public final static String SERVER_IP_AND_CHANNEL_ID_SEPARATOR = "##";
    public final static String REDIS_KEY_PREFIX = "CHANNEL-INFO";

    public static String getChannelByRedisKey(String key) {
        return ChannelUtil.REDIS_KEY_PREFIX + key;
    }

    public final static ExecutorService executor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


    public static List<String> getUserConnectionServerInfo(RedisUtil redisUtil, String userId) {
        List<String> userUserConnectionServerIP = redisUtil.range(getChannelByRedisKey(userId), 0, -1);
        return userUserConnectionServerIP;
    }
}
