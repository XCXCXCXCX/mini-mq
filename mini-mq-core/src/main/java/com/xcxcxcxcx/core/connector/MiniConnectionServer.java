package com.xcxcxcxcx.core.connector;

import com.xcxcxcxcx.core.connector.channel.ServerChannelHandler;
import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.ConnectionManager;
import com.xcxcxcxcx.mini.api.connector.message.PacketDispatcher;
import com.xcxcxcxcx.mini.common.handler.PullAckHandler;
import com.xcxcxcxcx.mini.common.handler.PullHandler;
import com.xcxcxcxcx.mini.common.handler.PushAckHandler;
import com.xcxcxcxcx.mini.common.handler.PushHandler;
import com.xcxcxcxcx.mini.common.message.wrapper.DefaultPacketDispatcher;
import com.xcxcxcxcx.mini.tools.config.MiniConfig;
import com.xcxcxcxcx.mini.tools.thread.ThreadPoolManager;
import com.xcxcxcxcx.network.connection.NettyConnectionManager;
import com.xcxcxcxcx.network.server.NettyTcpServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class MiniConnectionServer extends NettyTcpServer{


    private final ConnectionManager connectionManager;

    private final PacketDispatcher packetDispatcher;

    private final ChannelHandler channelHandler;

    private static final Boolean ENABLE_TRAFFIC_SHAPING = MiniConfig.mini.monitor.flowcontrol.enable_traffic_shaping;
    private static final long READ_CHANNEL_LIMIT = MiniConfig.mini.monitor.flowcontrol.read_channel_limit;
    private static final long WRITE_CHANNEL_LIMIT = MiniConfig.mini.monitor.flowcontrol.write_channel_limit;
    private static final long READ_GLOBAL_LIMIT = MiniConfig.mini.monitor.flowcontrol.read_global_limit;
    private static final long WRITE_GLOBAL_LIMIT = MiniConfig.mini.monitor.flowcontrol.write_global_limit;
    private static final long CHECK_INTERVAL = MiniConfig.mini.monitor.flowcontrol.check_interval;

    private static final int SEND_BUF_CAPACITY = MiniConfig.mini.connection.send_buf_capacity;
    private static final int RECEIVE_BUF_CAPACITY = MiniConfig.mini.connection.receive_buf_capacity;
    private static final int WRITE_BUFFER_LOW = MiniConfig.mini.connection.write_buffer_low;
    private static final int WRITE_BUFFER_HIGH = MiniConfig.mini.connection.write_buffer_high;

    /**
     * netty提供的流量整形
     */
    private GlobalChannelTrafficShapingHandler trafficShapingHandler;
    private ScheduledExecutorService trafficShapingExecutor;

    public MiniConnectionServer(int port) {
        this("localhost", port);
    }

    public MiniConnectionServer(String host, int port) {
        super(host, port);
        /**
         * connectionManager初始化
         */
        this.connectionManager = new NettyConnectionManager();

        /**
         * packetDispatcher初始化
         */
        this.packetDispatcher = new DefaultPacketDispatcher();
        packetDispatcher.register(Command.PUSH, new PushHandler());
        packetDispatcher.register(Command.PULL, new PullHandler());
        packetDispatcher.register(Command.PUSH_ACK_RESPONSE, new PushAckHandler());
        packetDispatcher.register(Command.PULL_ACK_RESPONSE, new PullAckHandler());

        /**
         * channelHandler初始化
         */
        this.channelHandler = new ServerChannelHandler(connectionManager, packetDispatcher);

        /**
         * trafficShapingExecutor和trafficShapingHandler初始化
         */
        if(ENABLE_TRAFFIC_SHAPING){
            trafficShapingExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadPoolManager("traffic-check"));
            trafficShapingHandler = new GlobalChannelTrafficShapingHandler(
                    trafficShapingExecutor,
                    WRITE_GLOBAL_LIMIT, READ_GLOBAL_LIMIT,
                    WRITE_CHANNEL_LIMIT, READ_CHANNEL_LIMIT,
                    CHECK_INTERVAL
            );
        }
    }

    @Override
    protected void initPipeline(ChannelPipeline pipeline) {
        super.initPipeline(pipeline);
        if (trafficShapingHandler != null) {
            pipeline.addFirst(trafficShapingHandler);
        }
    }

    @Override
    protected void addOption(ServerBootstrap b) {

        b.option(ChannelOption.SO_BACKLOG, 1024);

        /**
         * TCP层面的接收和发送缓冲区大小设置，
         * 在Netty中分别对应ChannelOption的SO_SNDBUF和SO_RCVBUF，
         * 需要根据推送消息的大小，合理设置，对于海量长连接，通常32K是个不错的选择。
         */
        if (SEND_BUF_CAPACITY > 0) b.childOption(ChannelOption.SO_SNDBUF, SEND_BUF_CAPACITY);
        if (RECEIVE_BUF_CAPACITY > 0) b.childOption(ChannelOption.SO_RCVBUF, RECEIVE_BUF_CAPACITY);

        /**
         * 这个坑其实也不算坑，只是因为懒，该做的事情没做。一般来讲我们的业务如果比较小的时候我们用同步处理，等业务到一定规模的时候，一个优化手段就是异步化。
         * 异步化是提高吞吐量的一个很好的手段。但是，与异步相比，同步有天然的负反馈机制，也就是如果后端慢了，前面也会跟着慢起来，可以自动的调节。
         * 但是异步就不同了，异步就像决堤的大坝一样，洪水是畅通无阻。如果这个时候没有进行有效的限流措施就很容易把后端冲垮。
         * 如果一下子把后端冲垮倒也不是最坏的情况，就怕把后端冲的要死不活。
         * 这个时候，后端就会变得特别缓慢，如果这个时候前面的应用使用了一些无界的资源等，就有可能把自己弄死。
         * 那么现在要介绍的这个坑就是关于Netty里的ChannelOutboundBuffer这个东西的。
         * 这个buffer是用在netty向channel write数据的时候，有个buffer缓冲，这样可以提高网络的吞吐量(每个channel有一个这样的buffer)。
         * 初始大小是32(32个元素，不是指字节)，但是如果超过32就会翻倍，一直增长。
         * 大部分时候是没有什么问题的，但是在碰到对端非常慢(对端慢指的是对端处理TCP包的速度变慢，比如对端负载特别高的时候就有可能是这个情况)的时候就有问题了，
         * 这个时候如果还是不断地写数据，这个buffer就会不断地增长，最后就有可能出问题了(我们的情况是开始吃swap，最后进程被linux killer干掉了)。
         * 为什么说这个地方是坑呢，因为大部分时候我们往一个channel写数据会判断channel是否active，但是往往忽略了这种慢的情况。
         *
         * 那这个问题怎么解决呢？其实ChannelOutboundBuffer虽然无界，但是可以给它配置一个高水位线和低水位线，
         * 当buffer的大小超过高水位线的时候对应channel的isWritable就会变成false，
         * 当buffer的大小低于低水位线的时候，isWritable就会变成true。所以应用应该判断isWritable，如果是false就不要再写数据了。
         * 高水位线和低水位线是字节数，默认高水位是64K，低水位是32K，我们可以根据我们的应用需要支持多少连接数和系统资源进行合理规划。
         */
        b.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(
                WRITE_BUFFER_LOW, WRITE_BUFFER_HIGH
        ));
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }
}
