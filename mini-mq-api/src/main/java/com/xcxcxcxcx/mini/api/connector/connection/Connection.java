package com.xcxcxcxcx.mini.api.connector.connection;

import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.session.SessionContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 *
 * 连接
 * @author XCXCXCXCX
 * @Since 1.0
 */
public interface Connection {

    byte STATUS_NEW = 0;
    byte STATUS_CONNECTED = 1;
    byte STATUS_DISCONNECTED = 2;

    /**
     * 关闭连接
     * @return
     */
    ChannelFuture close();

    /**
     * 获取会话上下文
     * @return
     */
    SessionContext getSessionContext();

    /**
     * 设置会话上下文
     * @param context
     */
    void setSessionContext(SessionContext context);

    /**
     * 发送数据包
     * @param packet
     * @return
     */
    ChannelFuture send(Packet packet);

    /**
     * 发送数据包并监听IO操作结果
     * @param packet
     * @param listener
     * @return
     */
    ChannelFuture send(Packet packet, ChannelFutureListener listener);

    /**
     * 获取connection全局ID
     * @return
     */
    String getId();

    /**
     * 是否已连接
     * @return
     */
    boolean isConnected();

    /**
     * 是否读超时
     * @return
     */
    boolean isReadTimeout();

    /**
     * 是否写超时
     * @return
     */
    boolean isWriteTimeout();

    /**
     * 更新上一次读时间
     */
    void updateLastReadTime();

    /**
     * 更新上一次写时间
     */
    void updateLastWriteTime();

    /**
     * 获取该连接的channel
     * @return
     */
    Channel getChannel();
}
