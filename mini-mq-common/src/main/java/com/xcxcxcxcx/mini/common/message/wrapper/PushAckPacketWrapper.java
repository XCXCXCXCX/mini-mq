package com.xcxcxcxcx.mini.common.message.wrapper;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.common.message.entity.PushAck;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class PushAckPacketWrapper extends BasePacketWrapper<PushAck>{

    public PushAckPacketWrapper(Connection connection, Packet packet) {
        super(connection, packet);
    }

    @Override
    protected PushAck decodeFromBody(Packet packet) {
        return jsonService.fromJson(packet.getBody(), PushAck.class);
    }
}
