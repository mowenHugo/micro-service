package com.hugo.common.pushcenter.provider.util;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author wangweiguang
 */
public class ChannelUtil {

    public final static String SERVER_IP_AND_CHANNEL_ID_SEPARATOR = "##";
    public final static String REDIS_KEY_PREFIX = "CHANNEL-INFO";

    public static String getChannelID(ChannelHandlerContext context) {
        String id = context.channel().id().asLongText();
        return id;
    }

    public static String getUserChannelRedisFlag(ChannelHandlerContext context) {
        String serverIPAndChannelId = getServerIP() +
                ChannelUtil.SERVER_IP_AND_CHANNEL_ID_SEPARATOR + ChannelUtil.getChannelID(context);
        return serverIPAndChannelId;
    }

    public static String getChannelByRedisKey(String key) {
        return ChannelUtil.REDIS_KEY_PREFIX + key;
    }

    public static Long cacheUserConnectionServerIP(RedisUtil redisUtil,
                                                   String userId, String serverIPAndChannelId) {
        long count = redisUtil.leftPush(getChannelByRedisKey(userId), serverIPAndChannelId);
        return count;
    }


    public static long removeUserConnectionServerIP(RedisUtil redisUtil, String userId, String serverIPAndChannelId) {
        long count = redisUtil.removeAllValue(getChannelByRedisKey(userId), serverIPAndChannelId);
        return count;
    }

    public static String getServerIP() {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
            String serverIP = inetAddress.getHostAddress();
            return serverIP;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
