package com.hugo.push.handler;

import com.google.common.base.Strings;
import com.hugo.push.client.WebSocketSession;
import com.hugo.push.server.WebSocketServer;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author wangweiguang
 */
public class HeartbeatKeepAliveHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatKeepAliveHandlerAdapter.class);

    //定期检测过期连接
    private final static int SCHEDULE_SECONDS = 5;
    private static ScheduledExecutorService scheduleService = Executors.newScheduledThreadPool(1);
    private static volatile boolean isSent = true;
    private static volatile int MAX_REPLY_PING = 5;
    //ping AND 已发送次数
    private static ConcurrentHashMap<String, Integer> pingPongChannelsMap = new ConcurrentHashMap<>();


    public HeartbeatKeepAliveHandlerAdapter() {
        logger.info("【成功启动心跳检测线程】");
    }

    static {
        scheduleService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isSent) {
                        isSent = false;
                        sendPingMessageToAll();
                    } else {
                        isSent = true;
                        clearNotPingPongMessage();
                    }
                } catch (Exception e) {
                    logger.error("心跳检查发生错误：", e.getMessage());
                }
            }
        }, 1L, SCHEDULE_SECONDS, TimeUnit.SECONDS);
    }


    public static void clearNotPingPongMessage() {
        Collection<WebSocketSession> sessions = WebSocketServer.connectSessionMaps.values();
        if (CollectionUtils.isEmpty(sessions)) {
            return;
        }
        for (WebSocketSession socketSession : sessions) {
            String id = socketSession.getChannelId();
            Integer pingTimes = HeartbeatKeepAliveHandlerAdapter.pingPongChannelsMap.get(id);
            if (pingTimes != null && pingTimes.intValue() >= MAX_REPLY_PING) {
                // close connection
                socketSession.sendMessage(new CloseWebSocketFrame());
                // clean client connection object
                HeartbeatKeepAliveHandlerAdapter.pingPongChannelsMap.remove(id);
                WebSocketSession webSocketSession = WebSocketServer.connectSessionMaps.remove(id);
                String userId = null;
                if (webSocketSession != null) {
                    userId = webSocketSession.getUserId();
                    webSocketSession = null;
                }
                if (!Strings.isNullOrEmpty(userId)) {
                    WebSocketServer.connectChannelMaps.remove(userId);
                }
            }
        }
    }

    public static void sendPingMessageToAll() {
        Collection<WebSocketSession> sessions = WebSocketServer.connectSessionMaps.values();
        if (CollectionUtils.isEmpty(sessions)) {
            return;
        }
        for (WebSocketSession socketSession : sessions) {
            PingWebSocketFrame ping = new PingWebSocketFrame();
            socketSession.sendMessage(ping);
            Integer pingTimes = HeartbeatKeepAliveHandlerAdapter.pingPongChannelsMap.get(socketSession.getChannelId());
            if (pingTimes != null) {
                HeartbeatKeepAliveHandlerAdapter.pingPongChannelsMap.put(socketSession.getChannelId(), pingTimes.intValue() + 1);
            } else {
                HeartbeatKeepAliveHandlerAdapter.pingPongChannelsMap.put(socketSession.getChannelId(), 1);
            }
        }
    }

    public static void receivePongMessage(String channelId) {
        HeartbeatKeepAliveHandlerAdapter.pingPongChannelsMap.remove(channelId);
    }
}
