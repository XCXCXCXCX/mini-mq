package com.xcxcxcxcx.mini.common.message.wrapper;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.wrapper.BasePacketWrapper;
import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePushAck;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class SettlePushAckPacketWrapper extends BasePacketWrapper<SettlePushAck> {

    public SettlePushAckPacketWrapper(Connection connection, Packet packet) {
        super(connection, packet);
    }

    @Override
    protected SettlePushAck decodeFromBody(Packet packet) {
        return jsonService.fromJson(packet.getBody(), SettlePushAck.class);
    }
}
