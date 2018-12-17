package com.xcxcxcxcx.mini.common.message.wrapper;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.wrapper.BasePacketWrapper;
import com.xcxcxcxcx.mini.api.connector.message.entity.Pull;

/**
 *
 * 消费消息请求
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PullPacketWrapper extends BasePacketWrapper<Pull> {

    public PullPacketWrapper(Connection connection, Packet packet) {
        super(connection, packet);
    }

    @Override
    protected Pull decodeFromBody(Packet packet) {
        byte[] body = packet.getBody();
        return jsonService.fromJson(body, Pull.class);
    }
}
