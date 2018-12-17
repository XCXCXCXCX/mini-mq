package com.xcxcxcxcx.mini.api.connector.message.wrapper;

import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePullAckResult;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class SettlePullAckResultPacketWrapper extends BasePacketWrapper<SettlePullAckResult> {

    public SettlePullAckResultPacketWrapper(Connection connection, Packet packet) {
        super(connection, packet);
    }

    @Override
    protected SettlePullAckResult decodeFromBody(Packet packet) {
        return jsonService.fromJson(packet.getBody(), SettlePullAckResult.class);
    }
}
