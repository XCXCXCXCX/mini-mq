package com.xcxcxcxcx.mini.common.message.wrapper;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.wrapper.BasePacketWrapper;
import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePullAck;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class SettlePullAckPacketWrapper extends BasePacketWrapper<SettlePullAck> {
    public SettlePullAckPacketWrapper(Connection connection, Packet packet) {
        super(connection, packet);
    }

    @Override
    protected SettlePullAck decodeFromBody(Packet packet) {
        return jsonService.fromJson(packet.getBody(), SettlePullAck.class);
    }
}
