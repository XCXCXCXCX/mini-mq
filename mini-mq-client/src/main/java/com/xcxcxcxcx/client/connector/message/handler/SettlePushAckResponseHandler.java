package com.xcxcxcxcx.client.connector.message.handler;

import com.xcxcxcxcx.client.storage.abs.MessageStorage;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePushAckResult;
import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import com.xcxcxcxcx.mini.api.connector.message.wrapper.SettlePushAckResultPacketWrapper;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class SettlePushAckResponseHandler extends BaseHandler {

    private final MessageStorage messageStorage;

    public SettlePushAckResponseHandler(MessageStorage messageStorage) {
        this.messageStorage = messageStorage;
    }

    @Override
    public void reply(Object result, Connection connection) {

    }

    @Override
    public Object doHandle(Packet packet, Connection connection) {
        SettlePushAckResult result = new SettlePushAckResultPacketWrapper(connection, packet).get();
        List<Long> ackIds = result.ackIds;

        messageStorage.remotePushAck(ackIds);

        return null;
    }
}
