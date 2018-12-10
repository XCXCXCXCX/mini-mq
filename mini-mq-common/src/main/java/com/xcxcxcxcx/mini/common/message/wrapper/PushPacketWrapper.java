package com.xcxcxcxcx.mini.common.message.wrapper;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.common.message.entity.Push;

/**
 *
 * send消息请求
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PushPacketWrapper extends BasePacketWrapper<Push>{

    public PushPacketWrapper(Connection connection, Packet packet) {
        super(connection, packet);
    }

    @Override
    protected Push decodeFromBody(Packet packet) {
        byte[] body = packet.getBody();
        return jsonService.fromJson(body, Push.class);
    }
}
