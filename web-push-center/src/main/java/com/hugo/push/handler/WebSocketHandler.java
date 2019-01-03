package com.hugo.push.handler;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hugo.push.client.DefaultWebSocketSession;
import com.hugo.push.client.WebSocketSession;
import com.hugo.push.constant.CustomResponseCode;
import com.hugo.push.server.WebSocketServer;
import com.hugo.push.util.ChannelUtil;
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
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;


/**
 * @Author wangweiguang
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(TextWebSocketFrame.class);

    private WebSocketServerHandshaker webSocketServerHandshaker;

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
            WebSocketServer.connectChannelMaps.remove(userId);
        }
        HeartbeatKeepAliveHandlerAdapter.receivePongMessage(channelId);
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
//        if (!request.method().name().equals(HttpMethod.POST)) {
//            sendHttpResponse(context,  request,
//                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
//            return;
//        }

        String token = requestUri.split("/")[1];
        if (Strings.isNullOrEmpty(token)) {
            sendHttpResponse(context, request,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //TODO 验证token，获取userId

        String userId = "12345";
        if (Strings.isNullOrEmpty(userId)) {
            sendHttpResponse(context, request,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
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
        //CONNECT
        webSocketServerHandshaker.handshake(context.channel(), request);

        DefaultWebSocketSession defaultWebSocketSession = new DefaultWebSocketSession();
        defaultWebSocketSession.setChannelId(ChannelUtil.getChannelID(context));
        defaultWebSocketSession.setChannel(context.channel());
        defaultWebSocketSession.setHandshakeHeaders(request.headers());
        defaultWebSocketSession.setUserId(userId);
        defaultWebSocketSession.setChannelHandlerContext(context);
        WebSocketServer.connectSessionMaps.put(ChannelUtil.getChannelID(context), defaultWebSocketSession);

        List<String> channels = WebSocketServer.connectChannelMaps.get(userId);
        if (!CollectionUtils.isEmpty(channels)) {
            channels.add(ChannelUtil.getChannelID(context));
        } else {
            channels = Lists.newArrayList(ChannelUtil.getChannelID(context));
        }
        WebSocketServer.connectChannelMaps.put(userId, channels);
    }

    private void processHandlerWebSocketFrame(ChannelHandlerContext context, WebSocketFrame message) throws Exception {
        String ChannelId = ChannelUtil.getChannelID(context);
        if (message instanceof CloseWebSocketFrame) {
            cleanUserInfo(context);
            HeartbeatKeepAliveHandlerAdapter.receivePongMessage(ChannelId);
            this.close(context);
        }
        if (message instanceof PongWebSocketFrame) {
            logger.info("{} 接收到客户端的PONG消息", new Date());
            HeartbeatKeepAliveHandlerAdapter.receivePongMessage(ChannelId);
            return;
        }
        if (!(message instanceof TextWebSocketFrame)) {
            logger.info("【不支持二进制数据传输】");
            throw new UnsupportedOperationException("不支持二进制数据传输");
        }

        TextWebSocketFrame t = (TextWebSocketFrame) message;
        String text = t.text();
        logger.info("{} 接收到客户端的消息: {}", new Date(), text);
        HeartbeatKeepAliveHandlerAdapter.receivePongMessage(ChannelId);
        context.channel().writeAndFlush(new TextWebSocketFrame("服务在 " + new Date() + " 接受到你的信息为：" + text));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Object message) throws Exception {
        if (message instanceof FullHttpRequest) {
            processHandlerHttpRequest(context, (FullHttpRequest) message);
        } else if (message instanceof WebSocketFrame) {
            processHandlerWebSocketFrame(context, (WebSocketFrame) message);
        }
    }
}
