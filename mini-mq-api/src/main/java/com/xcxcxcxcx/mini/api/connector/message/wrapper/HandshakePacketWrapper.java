package com.xcxcxcxcx.mini.api.connector.message.wrapper;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.Handshake;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class HandshakePacketWrapper extends BasePacketWrapper<Handshake>{

    public HandshakePacketWrapper(Connection connection, Packet packet) {
        super(connection, packet);
    }

    @Override
    protected Handshake decodeFromBody(Packet packet) {
        return jsonService.fromJson(packet.getBody(), Handshake.class);
    }
}
