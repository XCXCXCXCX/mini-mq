package com.xcxcxcxcx.mini.api.connector.message.wrapper;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.HandshakeOK;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class HandshakeOKPacketWrapper extends BasePacketWrapper<HandshakeOK>{

    public HandshakeOKPacketWrapper(Connection connection, Packet packet) {
        super(connection, packet);
    }

    @Override
    protected HandshakeOK decodeFromBody(Packet packet) {
        return jsonService.fromJson(packet.getBody(), HandshakeOK.class);
    }
}
