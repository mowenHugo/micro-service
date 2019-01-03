package com.hugo.push.util;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author wangweiguang
 */
public class ChannelUtil {

    public static String getChannelID(ChannelHandlerContext context) {
        if (context == null) {
            return null;
        }
        String id = context.channel().id().asLongText();
        return id;
    }
}
