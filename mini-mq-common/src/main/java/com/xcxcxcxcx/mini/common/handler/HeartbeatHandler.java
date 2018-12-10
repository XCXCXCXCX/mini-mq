package com.xcxcxcxcx.mini.common.handler;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.PacketHandler;
import com.xcxcxcxcx.mini.tools.log.LogUtils;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class HeartbeatHandler implements PacketHandler{

    @Override
    public void reply(Object result, Connection connection) {
        connection.send((Packet)result);
    }

    @Override
    public Object doHandle(Packet packet, Connection connection) {
        LogUtils.handler.info("receive heartbeat packet , {}", connection);
        return packet;
    }
}
