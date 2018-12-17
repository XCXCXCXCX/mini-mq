package com.xcxcxcxcx.mini.common.message.wrapper;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.wrapper.BasePacketWrapper;
import com.xcxcxcxcx.mini.api.connector.message.entity.PullAck;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PullAckPacketWrapper extends BasePacketWrapper<PullAck> {

    public PullAckPacketWrapper(Connection connection, Packet packet) {
        super(connection, packet);
    }

    @Override
    protected PullAck decodeFromBody(Packet packet) {
        return jsonService.fromJson(packet.getBody(), PullAck.class);
    }
}
