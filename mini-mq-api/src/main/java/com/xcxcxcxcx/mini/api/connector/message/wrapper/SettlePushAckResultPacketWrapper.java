package com.xcxcxcxcx.mini.api.connector.message.wrapper;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePushAckResult;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class SettlePushAckResultPacketWrapper extends BasePacketWrapper<SettlePushAckResult> {

    public SettlePushAckResultPacketWrapper(Connection connection, Packet packet) {
        super(connection, packet);
    }

    @Override
    protected SettlePushAckResult decodeFromBody(Packet packet) {
        return jsonService.fromJson(packet.getBody(), SettlePushAckResult.class);
    }
}
