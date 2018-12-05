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
    void handle(Packet packet, Connection connection);

    /**
     * 响应结果
     * @return
     */
    void reply(Object result, Connection connection);

    Object doHandle(Packet packet, Connection connection);
}
