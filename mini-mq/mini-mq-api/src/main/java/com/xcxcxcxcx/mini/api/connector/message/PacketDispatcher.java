package com.xcxcxcxcx.mini.api.connector.message;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.command.Command;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface PacketDispatcher {

    /**
     * 注册cmd与handler的映射关系
     *
     * @param command
     * @param handler
     */
    void register(Command command, PacketHandler handler);

    /**
     * 分发message给不同的handler处理
     * @param packet
     * @param connection
     */
    void dispatch(Packet packet, Connection connection);
}
