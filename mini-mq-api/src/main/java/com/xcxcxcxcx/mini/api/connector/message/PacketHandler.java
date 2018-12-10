package com.xcxcxcxcx.mini.api.connector.message;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface PacketHandler {

    /**
     * 处理message
     * @param packet
     * @param connection
     */
    default void handle(Packet packet, Connection connection){
        //消息处理，一般是将topic下的partition消息取出来
        Object result = doHandle(packet, connection);

        //一般是响应消息处理结果给对端
        reply(result, connection);
    }

    /**
     * 响应结果
     * @return
     */
    void reply(Object result, Connection connection);

    Object doHandle(Packet packet, Connection connection);
}
