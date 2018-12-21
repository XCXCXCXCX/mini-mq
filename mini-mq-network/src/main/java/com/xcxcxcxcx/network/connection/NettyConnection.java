package com.xcxcxcxcx.network.connection;

import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.session.SessionContext;
import com.xcxcxcxcx.mini.tools.log.LogUtils;
import com.xcxcxcxcx.network.connection.session.ServerSessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NettyConnection ，用于封装channel，提供心跳检测功能
 *
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class NettyConnection implements Connection, ChannelFutureListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyConnection.class);

    private static final long READ_TIME_OUT = SessionContext.heartbeat + 1000;

    private static final long WRITE_TIME_OUT = SessionContext.heartbeat - 1000;

    private SessionContext context;
    private Channel channel;
    private volatile byte status = STATUS_NEW;
    private long lastReadTime;
    private long lastWriteTime;

    /**
     * 创建连接
     *
     * @param channel
     */
    public NettyConnection(Channel channel) {
        this.channel = channel;
        this.context = ServerSessionManager.newSession();
        long current = System.currentTimeMillis();
        this.lastReadTime = current;
        this.lastWriteTime = current;
        this.status = STATUS_CONNECTED;
    }

    @Override
    public ChannelFuture close() {
        if (status == STATUS_DISCONNECTED) return null;
        this.status = STATUS_DISCONNECTED;
        return this.channel.close();
    }

    @Override
    public SessionContext getSessionContext() {
        return context;
    }

    @Override
    public void setSessionContext(SessionContext context) {
        this.context = context;
    }

    /**
     * 异步发送，不监听结果
     *
     * @param packet
     * @return
     */
    @Override
    public ChannelFuture send(Packet packet) {
        return send(packet, null);
    }

    /**
     * 异步发送，监听结果
     *
     * @param packet
     * @param listener
     * @return
     */
    @Override
    public ChannelFuture send(Packet packet, ChannelFutureListener listener) {
        if (getChannel().isActive()) {
            ChannelFuture future = getChannel().writeAndFlush(packet.completeHeader(getSessionContext().getSessionId()))
                    .addListener(this);

            if (listener != null) {
                future.addListener(listener);
            }

            //1.如果channel不是可写的(说明此时IO操作频繁，IO被占用)
            //  且
            //2.如果channel不是由该线程处理或指定处理线程还未启动
            //channel注册流程异步进行
            if (!getChannel().isWritable() && !future.channel().eventLoop().inEventLoop()) {
                //1.阻塞当前线程，等待线程处理完毕（等待100ms）
                if (future.awaitUninterruptibly(100)) {
                    return future;
                }

                //2.悲观的认为短时间无法处理，使用promise保证异步线程抛异常
                getChannel().newPromise().setFailure(new RuntimeException("send data too busy"));
            }

            //1.如果channel是可写的
            //  或
            //2.如果channel是由该线程处理，则会同步执行channel注册流程
            return future;

        } else {
            return close();
        }

    }

    @Override
    public String getId() {
        return getChannel().id().asShortText();
    }

    @Override
    public boolean isConnected() {
        return this.status == STATUS_CONNECTED;
    }

    @Override
    public boolean isReadTimeout() {
        return System.currentTimeMillis() - lastReadTime > READ_TIME_OUT;
    }

    @Override
    public boolean isWriteTimeout() {
        return System.currentTimeMillis() - lastWriteTime > WRITE_TIME_OUT;
    }

    @Override
    public void updateLastReadTime() {
        this.lastReadTime = System.currentTimeMillis();
    }

    @Override
    public void updateLastWriteTime() {
        this.lastWriteTime = System.currentTimeMillis();
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    /**
     * 监听channel处理结果，channel写操作结果的回调
     *
     * @param future
     * @throws Exception
     */
    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
            lastWriteTime = System.currentTimeMillis();
        } else {
            LOGGER.error("connection send msg error", future.cause());
            LogUtils.connection.error("connection send msg error={}, conn={}", future.cause().getMessage(), this);
        }
    }


}
