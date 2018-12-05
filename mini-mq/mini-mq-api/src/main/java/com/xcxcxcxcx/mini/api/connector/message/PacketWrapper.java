package com.xcxcxcxcx.mini.api.connector.message;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import io.netty.channel.ChannelFutureListener;

/**
 *
 * 消息
 *
 * push/pull/heartbeat/ack
 * @author XCXCXCXCX
 * @Since 1.0
 */
public interface PacketWrapper<T> {

    /**
     * 每个message绑定一个连接
     * @return
     */
    Connection getConnection();

    /**
     * 每个message里面包裹了一个packet
     * @return
     */
    Packet getPacket();

    /**
     * message对外是可见的消息对象
     * @return 返回decode后的packet.body
     */
    T get();

    /**
     * 发送encode后的packet.body
     * @param listener
     */
    void send(ChannelFutureListener listener);
}
