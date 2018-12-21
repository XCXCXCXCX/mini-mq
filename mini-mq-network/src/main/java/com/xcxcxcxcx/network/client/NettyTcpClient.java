package com.xcxcxcxcx.network.client;

import com.xcxcxcxcx.mini.api.event.service.BaseService;
import com.xcxcxcxcx.mini.api.event.service.Client;
import com.xcxcxcxcx.mini.api.event.service.Listener;
import com.xcxcxcxcx.mini.api.event.service.Server;
import com.xcxcxcxcx.mini.tools.thread.ThreadPoolManager;
import com.xcxcxcxcx.network.codec.PacketDecoder;
import com.xcxcxcxcx.network.codec.PacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 *
 * Netty客户端
 * @author XCXCXCXCX
 * @since 1.0
 */
public abstract class NettyTcpClient extends BaseService implements Client {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private String host;
    private final int port;
    private EventLoopGroup workerGroup;
    private Bootstrap bootstrap;
    private GenericFutureListener extentionListener;

    private static final int DEFAULT_WORKER_THREAD_NUM = 1;

    private static final int DEFAULT_WORKER_IO_RATIO = 50;

    public NettyTcpClient(int port) {
        this(null, port,null);
    }

    public NettyTcpClient(String host, int port, GenericFutureListener listener) {
        this.host = host;
        this.port = port;
        this.extentionListener = listener;
    }

    @Override
    public void connect() {
        if(bootstrap == null){
            throw new IllegalStateException("client has not been inited :" + this.getClass().getName());
        }
        final InetSocketAddress address =
                (host == null||"".equals(host)) ?
                        new InetSocketAddress(port) : new InetSocketAddress(host, port);

        doConnect(address);
    }

    private void doConnect(InetSocketAddress address) {
        try {
            bootstrap.connect(address).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(future.isSuccess()){
                        LOGGER.info("client connect " + address.toString() + " success");
                    }else{
                        LOGGER.error("client connect " + address.toString() + " error");
                    }
                }
            }).addListener(extentionListener);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("重新连接broker时出错");
        }
    }

    public void reconnect(InetSocketAddress address) {
        doConnect(address);
    }

    /**
     * 初始化阶段，初始化boss线程组和worker线程组
     */
    @Override
    public void init() {
        EventLoopGroup workerGroup = this.workerGroup;
        if(workerGroup == null){
            NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(DEFAULT_WORKER_THREAD_NUM, getWorkThreadFactory(), getSelectorProvider());
            nioEventLoopGroup.setIoRatio(DEFAULT_WORKER_IO_RATIO);
            this.workerGroup = nioEventLoopGroup;
        }

        bootstrap = new Bootstrap();
    }

    private ThreadFactory getWorkThreadFactory() {
        return new ThreadPoolManager("network-client-worker");
    }

    private SelectorProvider getSelectorProvider(){
        return SelectorProvider.provider();
    }

    @Override
    public void destroy() {
        this.bootstrap = null;
        this.workerGroup = null;
    }

    @Override
    public void doStart() {
        EventLoopGroup workerGroup = this.workerGroup;

        bootstrap.group(workerGroup)
                .channelFactory(getChannelFactory())
                .handler(getChannelInitializer())
                .option(ChannelOption.SO_REUSEADDR, true)//允许重用地址，允许同一主机建立多个连接
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 4000)//超时
                .option(ChannelOption.TCP_NODELAY, true)//启用Nagle算法
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);//开启内存空间

        connect();

    }

    /**
     * 定义channel初始化链路
     * @return
     */
    private ChannelHandler getChannelInitializer() {
        return new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                initPipeline(ch);
            }
        };
    }

    /**
     * 初始化pipeline
     * @param ch
     */
    private void initPipeline(Channel ch) {

        ch.pipeline()
                .addLast("decoder", getDecoder())
                .addLast("encoder", getEncoder())
                .addLast("handler", getChannelHandler());

    }

    /**
     * 解码器
     * @return
     */
    private ChannelHandler getDecoder(){
        return new PacketDecoder(3*1024*1024,
                0,4,
                1, 9,
                true);
    }

    /**
     * 编码器
     * @return
     */
    private ChannelHandler getEncoder(){
        return new PacketEncoder();
    }

    /**
     * ChannelHandler
     * @return
     */
    public abstract ChannelHandler getChannelHandler();

    /**
     * 获取channel的方式：new NioSocketChannel()
     * @return
     */
    private ChannelFactory<? extends Channel> getChannelFactory() {
        return NioSocketChannel::new;
    }

    @Override
    public void doStop() {
        try {
            workerGroup.shutdownGracefully(10, 60, TimeUnit.SECONDS).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bootstrap = null;
        }
    }
}
