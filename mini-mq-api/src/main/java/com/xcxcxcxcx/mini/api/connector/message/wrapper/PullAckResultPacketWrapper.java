package com.xcxcxcxcx.mini.api.connector.message.wrapper;

import com.xcxcxcxcx.mini.api.connector.message.entity.PullAckResult;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PullAckResultPacketWrapper extends BasePacketWrapper<PullAckResult> {

    public PullAckResultPacketWrapper(Connection connection, Packet packet) {
        super(connection, packet);
    }

    @Override
    protected PullAckResult decodeFromBody(Packet packet) {
        return jsonService.fromJson(packet.getBody(), PullAckResult.class);
    }
}
