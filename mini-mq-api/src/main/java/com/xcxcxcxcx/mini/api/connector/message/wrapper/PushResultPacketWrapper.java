package com.xcxcxcxcx.mini.api.connector.message.wrapper;


import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.PushResult;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PushResultPacketWrapper extends BasePacketWrapper<PushResult> {


    public PushResultPacketWrapper(Connection connection, Packet packet) {
        super(connection, packet);
    }

    @Override
    protected PushResult decodeFromBody(Packet packet) {
        return jsonService.fromJson(packet.getBody(), PushResult.class);
    }
}
