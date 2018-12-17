package com.xcxcxcxcx.mini.api.connector.message.wrapper;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.PushAckResult;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PushAckResultPacketWrapper extends BasePacketWrapper<PushAckResult> {


    public PushAckResultPacketWrapper(Connection connection, Packet packet) {
        super(connection, packet);
    }

    @Override
    protected PushAckResult decodeFromBody(Packet packet) {
        return jsonService.fromJson(packet.getBody(), PushAckResult.class);
    }
}
