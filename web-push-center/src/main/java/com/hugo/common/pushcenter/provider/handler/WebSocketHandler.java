package com.hugo.common.pushcenter.provider.handler;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hugo.common.pushcenter.provider.client.WebSocketSession;
import com.hugo.common.pushcenter.provider.client.DefaultWebSocketSession;
import com.hugo.common.pushcenter.provider.constant.CustomResponseCode;
import com.hugo.common.pushcenter.provider.server.WebSocketServer;
import com.hugo.common.pushcenter.provider.util.ChannelUtil;
import com.hugo.common.pushcenter.provider.util.RedisUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Author wangweiguang
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(TextWebSocketFrame.class);
    private WebSocketServerHandshaker webSocketServerHandshaker;
    private static ApplicationContext applicationContext;
    private RedisUtil redisUtil;
    private static volatile int MAX_REPLY_PING = 5;
    //ping次数，channelId and ping count
    private static ConcurrentHashMap<String, Integer> pingCountByChannelMap = new ConcurrentHashMap<>();

    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketHandler.applicationContext = applicationContext;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("【handlerAdded】===>" + ctx.channel().id());
    }

    private void cleanUserInfo(ChannelHandlerContext context) {
        String channelId = ChannelUtil.getChannelID(context);
        WebSocketSession webSocketSession = WebSocketServer.connectSessionMaps.get(channelId);
        String userId = null;
        if (webSocketSession != null) {
            userId = webSocketSession.getUserId();
            WebSocketServer.connectSessionMaps.remove(channelId);
            webSocketSession = null;
        }
        if (!Strings.isNullOrEmpty(userId)) {
            //这里的代码在同一用户打开多个浏览器时，在极端情况下，该用户在同一时刻都关闭通道时，会存在数据一致性问题。
            //不同用户之间不会有数字一致性问题。
            List<String> channels = WebSocketServer.connectChannelMaps.get(userId);
            channels.remove(channelId);
            WebSocketServer.connectChannelMaps.put(userId, channels);
            ChannelUtil.removeUserConnectionServerIP(redisUtil, userId, ChannelUtil.getUserChannelRedisFlag(context));
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) throws Exception {
        logger.info("【handlerRemoved】===>" + context.channel().id());
        cleanUserInfo(context);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        logger.error("【exceptionCaught】===>" + cause.getMessage());
        cleanUserInfo(context);
        context.close();
        context.channel().close();
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        logger.info("【channelActive】===>" + context.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        logger.info("【channelInactive】===>" + context.channel().id());
        cleanUserInfo(context);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception {
        context.flush();
    }

    public void close(ChannelHandlerContext context) throws Exception {
        if (this.webSocketServerHandshaker != null
                && context != null && context.channel().isOpen()) {
            CloseWebSocketFrame closeWebSocketFrame = new CloseWebSocketFrame(
                    CustomResponseCode.NORMAL_CLOSE.getCode(), CustomResponseCode.NORMAL_CLOSE.getMessage());
            this.webSocketServerHandshaker.close(context.channel(), closeWebSocketFrame);
        }
    }


    private static void sendHttpResponse(
            ChannelHandlerContext context, FullHttpRequest req, DefaultFullHttpResponse res) {
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如果不是Keep-Alive，直接关闭连接
        ChannelFuture f = context.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void processHandlerHttpRequest(ChannelHandlerContext context, FullHttpRequest request) throws Exception {
        String requestUri = request.uri();
        logger.warn("http channelRead request uri:【{}】", requestUri);
        if (Strings.isNullOrEmpty(requestUri)) {
            sendHttpResponse(context, request,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        logger.info("request.method().name(): " + request.method().name());

        String token = requestUri.split("/")[1];
        if (Strings.isNullOrEmpty(token)) {
            sendHttpResponse(context, request,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        String userId = "12345";
        // 验证token，获取userId
//        userInfoService = applicationContext.getBean(UserInfoService.class);
//        String userId = userInfoService.getUserIdByToken(token);
//        if (Strings.isNullOrEmpty(userId)) {
//            sendHttpResponse(context, request,
//                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
//            return;
//        }
        // http解码失败
        if (!request.decoderResult().isSuccess() || (!"websocket".equals(request.headers().get("Upgrade")))) {
            sendHttpResponse(context, request,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
        decoder.offer(request);
        List<InterfaceHttpData> parameterList = decoder.getBodyHttpDatas();
        for (InterfaceHttpData parameter : parameterList) {
            Attribute data = (Attribute) parameter;
            logger.warn("http request name: 【{}】", data.getName());
            logger.warn("http request value: 【{}】", data.getValue());
        }

        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(
                "ws://" + request.headers().get("Host") + "/" + token, null, false
        );

        webSocketServerHandshaker = factory.newHandshaker(request);
        if (webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(context.channel());
        }

        buildWebSocketConnection(context, request, userId);
    }

    private void buildWebSocketConnection(ChannelHandlerContext context, FullHttpRequest request, String userId) {
        String serverIP = ChannelUtil.getServerIP();
        if (serverIP == null) {
            sendHttpResponse(context, request,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR));
        }
        //CONNECT
        webSocketServerHandshaker.handshake(context.channel(), request);

        DefaultWebSocketSession defaultWebSocketSession = new DefaultWebSocketSession();
        defaultWebSocketSession.setChannelId(ChannelUtil.getChannelID(context));
        defaultWebSocketSession.setChannel(context.channel());
        defaultWebSocketSession.setHandshakeHeaders(request.headers());
        defaultWebSocketSession.setUserId(userId);
        defaultWebSocketSession.setChannelHandlerContext(context);

        WebSocketServer.connectSessionMaps.put(ChannelUtil.getChannelID(context), defaultWebSocketSession);
        //这里的代码在同一用户打开多个浏览器时，极端情况下，在同一时刻都建立连接，会存在数据一致性问题。
        //但同一个用户总会有一个连接会连接上，不影响使用，不同用户之间不会有数字一致性问题。
        List<String> channels = WebSocketServer.connectChannelMaps.get(userId);
        if (!CollectionUtils.isEmpty(channels)) {
            channels.add(ChannelUtil.getChannelID(context));
        } else {
            channels = Lists.newArrayList(ChannelUtil.getChannelID(context));
        }
        WebSocketServer.connectChannelMaps.put(userId, channels);
        redisUtil = applicationContext.getBean(RedisUtil.class);
        ChannelUtil.cacheUserConnectionServerIP(redisUtil, userId, ChannelUtil.getUserChannelRedisFlag(context));
    }

    private void processHandlerWebSocketFrame(ChannelHandlerContext context, WebSocketFrame message) throws Exception {
        String ChannelId = ChannelUtil.getChannelID(context);
        this.receivePongMessage(ChannelId);
        if (message instanceof CloseWebSocketFrame) {
            cleanUserInfo(context);
            this.receivePongMessage(ChannelId);
            this.close(context);
            return;
        }
        if (message instanceof PongWebSocketFrame) {
            logger.info("{} 接收到客户端【{}】的PONG消息", new Date(), context.channel().id());
            return;
        }
        if (!(message instanceof TextWebSocketFrame)) {
            logger.info("【不支持二进制数据传输】");
            throw new UnsupportedOperationException("不支持二进制数据传输");
        }

//        TextWebSocketFrame t = (TextWebSocketFrame) message;
//        String text = t.text();
//        logger.info("{} 接收到客户端【{}】的消息: {}", new Date(), context.channel().id(), text);
//        context.channel().writeAndFlush(new TextWebSocketFrame("服务器在 " + new Date() + " 接受到你的信息为：" + text));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Object message) throws Exception {
        if (message instanceof FullHttpRequest) {
            processHandlerHttpRequest(context, (FullHttpRequest) message);
        } else if (message instanceof WebSocketFrame) {
            processHandlerWebSocketFrame(context, (WebSocketFrame) message);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext context, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent stateEvent = (IdleStateEvent) evt;
            String channelId = ChannelUtil.getChannelID(context);
            WebSocketSession webSocketSession = WebSocketServer.connectSessionMaps.get(channelId);
            if (webSocketSession == null) {
                return;
            }

            Integer pingTimes = WebSocketHandler.pingCountByChannelMap.get(channelId);
            if (pingTimes == null) {
                WebSocketHandler.pingCountByChannelMap.put(webSocketSession.getChannelId(), 1);
                pingTimes = 0;
            }

            if (pingTimes.intValue() >= MAX_REPLY_PING) {
                webSocketSession.sendMessage(new CloseWebSocketFrame());
                WebSocketHandler.pingCountByChannelMap.remove(channelId);
                cleanUserInfo(context);
                return;
            }

            PingWebSocketFrame ping = new PingWebSocketFrame();
            switch (stateEvent.state()) {
                case READER_IDLE:
                    logger.info("【" + context.channel().remoteAddress() + "】 readerIdle ...");
                    context.writeAndFlush(ping);
                    break;
                case WRITER_IDLE:
                    logger.info("【" + context.channel().remoteAddress() + "】 writerIdle ...");
                    context.writeAndFlush(ping);
                    break;
                case ALL_IDLE:
                    logger.info("【" + context.channel().remoteAddress() + "】readerIdle and writerIdle");
                    break;
            }
            WebSocketHandler.pingCountByChannelMap.put(webSocketSession.getChannelId(), pingTimes.intValue() + 1);
        }
    }

    private void receivePongMessage(String channelId) {
        WebSocketHandler.pingCountByChannelMap.remove(channelId);
    }
}
