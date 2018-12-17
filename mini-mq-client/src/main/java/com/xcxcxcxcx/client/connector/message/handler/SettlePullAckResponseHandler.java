package com.xcxcxcxcx.client.connector.message.handler;

import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePullAckResult;
import com.xcxcxcxcx.mini.api.connector.message.wrapper.SettlePullAckResultPacketWrapper;
import com.xcxcxcxcx.client.storage.abs.MessageStorage;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class SettlePullAckResponseHandler extends BaseHandler {

    private final MessageStorage messageStorage;

    public SettlePullAckResponseHandler(MessageStorage messageStorage) {
        this.messageStorage = messageStorage;
    }


    @Override
    public void reply(Object result, Connection connection) {

    }

    @Override
    public Object doHandle(Packet packet, Connection connection) {

        SettlePullAckResult result = new SettlePullAckResultPacketWrapper(connection, packet).get();
        List<Long> ackIds = result.ackIds;
        List<Long> rejectIds = result.rejectIds;

        messageStorage.remotePullAck(ackIds);
        messageStorage.remotePullReject(rejectIds);

        return null;
    }
}
