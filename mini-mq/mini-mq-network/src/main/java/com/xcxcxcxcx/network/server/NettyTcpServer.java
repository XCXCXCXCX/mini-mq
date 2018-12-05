package com.xcxcxcxcx.network.server;

import com.xcxcxcxcx.mini.api.event.service.BaseService;
import com.xcxcxcxcx.mini.api.event.service.Server;
import com.xcxcxcxcx.mini.tools.thread.ThreadPoolManager;
import com.xcxcxcxcx.network.codec.PacketDecoder;
import com.xcxcxcxcx.network.codec.PacketEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Netty客户端
 * @author XCXCXCXCX
 * @since 1.0
 */
public abstract class NettyTcpServer extends BaseService implements Server {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String host;
    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;

    private static final int DEFAULT_BOSS_THREAD_NUM = 1;
    private static final int DEFAULT_WORKER_THREAD_NUM = 2;

    private static final int DEFAULT_BOSS_IO_RATIO = 100;
    private static final int DEFAULT_WORKER_IO_RATIO = 70;

    public NettyTcpServer(int port) {
        this.port = port;
    }

    public NettyTcpServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void bind() {
        if(bootstrap == null){
            throw new IllegalStateException("server has not been inited :" + this.getClass().getName());
        }
        final InetSocketAddress address =
                (host == null||"".equals(host)) ?
                        new InetSocketAddress(port) : new InetSocketAddress(host, port);
        bootstrap.bind(address).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if(future.isSuccess()){
                    logger.info("server bind " + address.getAddress() + " success");
                }else{
                    logger.error("server bind " + address.getAddress() + " error");
                }
            }
        });
    }

    /**
     * 初始化阶段，初始化boss线程组和worker线程组
     */
    @Override
    public void init() {
        EventLoopGroup bossGroup = this.bossGroup;
        EventLoopGroup workerGroup = this.workerGroup;
        if (bossGroup == null){
            NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(DEFAULT_BOSS_THREAD_NUM, getBossThreadFactory(), getSelectorProvider());
            nioEventLoopGroup.setIoRatio(DEFAULT_BOSS_IO_RATIO);
            this.bossGroup = nioEventLoopGroup;
        }
        if(workerGroup == null){
            NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(DEFAULT_WORKER_THREAD_NUM, getWorkThreadFactory(), getSelectorProvider());
            nioEventLoopGroup.setIoRatio(DEFAULT_WORKER_IO_RATIO);
            this.workerGroup = nioEventLoopGroup;
        }

        bootstrap = new ServerBootstrap();
    }

    private ThreadFactory getBossThreadFactory(){
        return new ThreadPoolManager("network-server-boss");
    }

    private ThreadFactory getWorkThreadFactory() {
        return new ThreadPoolManager("network-server-worker");
    }

    private SelectorProvider getSelectorProvider(){
        return SelectorProvider.provider();
    }

    @Override
    public void destroy() {
        this.bootstrap = null;
        this.bossGroup = null;
        this.workerGroup = null;
    }

    @Override
    public void doStart() {
        EventLoopGroup bossGroup = this.bossGroup;
        EventLoopGroup workerGroup = this.workerGroup;

        bootstrap.group(bossGroup, workerGroup)
                .channelFactory(getChannelFactory())
                .childHandler(getChannelInitializer())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        bind();

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
        return new PacketDecoder();
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
     * 获取channel的方式：new NioServerSocketChannel()
     * @return
     */
    private ChannelFactory<? extends ServerChannel> getChannelFactory() {
        return NioServerSocketChannel::new;
    }

    @Override
    public void doStop() {
        try {
            bossGroup.shutdownGracefully(10, 60, TimeUnit.SECONDS).sync();
            workerGroup.shutdownGracefully(10, 60, TimeUnit.SECONDS).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bootstrap = null;
        }
    }
}
