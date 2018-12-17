package com.xcxcxcxcx.mini.api.connector.message.wrapper;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.PullResult;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PullResultPacketWrapper extends BasePacketWrapper<PullResult> {

    public PullResultPacketWrapper(Connection connection, Packet packet) {
        super(connection, packet);
    }

    @Override
    protected PullResult decodeFromBody(Packet packet) {
        return jsonService.fromJson(packet.getBody(), PullResult.class);
    }
}
