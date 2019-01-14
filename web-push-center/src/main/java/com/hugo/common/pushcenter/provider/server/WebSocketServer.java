package com.hugo.common.pushcenter.provider.server;

import com.hugo.common.pushcenter.provider.client.WebSocketSession;
import com.hugo.common.pushcenter.provider.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author wangweiguang
 */
@Component
public class WebSocketServer implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private int AVAILABLE_THREADS = Runtime.getRuntime().availableProcessors();
    private EventLoopGroup bossGroup = new NioEventLoopGroup(AVAILABLE_THREADS);
    private EventLoopGroup workGroup = new NioEventLoopGroup(2 * AVAILABLE_THREADS);
    private ServerBootstrap bootstrap;
    private AtomicBoolean closeStatus = new AtomicBoolean(false);
    public static ConcurrentHashMap<String, WebSocketSession> connectSessionMaps = new ConcurrentHashMap<>(); // ChannelId对应的WebSocketSession
    public static ConcurrentHashMap<String, List<String>> connectChannelMaps = new ConcurrentHashMap<>(); // userId对应的channelId

    @Value("${web.server.socket.port}")
    private int port;

    @Override
    public void afterPropertiesSet() throws Exception {
        startServer();
    }

    public void startServer() throws Exception {
        if (closeStatus.compareAndSet(true, false)) {
            return;
        }
        try {
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new WebChannelHandler())
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_LINGER, 0)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            start();
            logger.info("【服务器启动成功，端口：" + port + "】");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            destroy();
        }
    }

    public void start() throws Exception {
        if (closeStatus.compareAndSet(true, false)) {
            return;
        }
        closeStatus.getAndSet(true);
        bootstrap.bind(new InetSocketAddress(port)).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) {
                if (channelFuture.isSuccess()) {
                    closeStatus.getAndSet(true);
                    logger.warn("webSocket started success, waiting for client connect...");
                } else {
                    logger.warn("webSocket started failed");
                    closeStatus.getAndSet(false);
                    channelFuture.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                start();
                            } catch (Exception e) {
                                logger.warn("webSocket restart failed");
                                e.printStackTrace();
                            }
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        }).sync();//等待
        logger.warn("Starting websocekt... Port: " + port);
    }

    @Override
    public void destroy() throws Exception {
        if (closeStatus.compareAndSet(true, false)) {
            return;
        }
        logger.warn("webSocket closed");
        if (null != bossGroup) {
            bossGroup.shutdownGracefully();
        }
        if (null != workGroup) {
            workGroup.shutdownGracefully();
        }
    }

    class WebChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast("http-codec", new HttpServerCodec());
            pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
            pipeline.addLast("http-chunked", new ChunkedWriteHandler());
            pipeline.addLast(new IdleStateHandler(120, 30, 60 * 30, TimeUnit.SECONDS));
            pipeline.addLast((new WebSocketHandler()));
        }
    }
}
