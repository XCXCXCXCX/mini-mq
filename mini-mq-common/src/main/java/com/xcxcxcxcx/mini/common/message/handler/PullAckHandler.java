package com.xcxcxcxcx.mini.common.message.handler;

import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.PullAckResult;
import com.xcxcxcxcx.mini.api.connector.message.entity.PullAck;
import com.xcxcxcxcx.mini.common.message.wrapper.PullAckPacketWrapper;

import java.util.List;


/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PullAckHandler extends AckHandler{

    @Override
    public Object doHandle(Packet packet, Connection connection) {

        PullAck pullAck = new PullAckPacketWrapper(connection, packet).get();
        List<Long> ackIds = pullAck.messageAckIds;
        List<Long> rejectIds = pullAck.messageRejectIds;
        String groupId = connection.getSessionContext().getId();

        PullAckResult ackResult = new PullAckResult();
        ackResult.messageAckIds = persistenceMapper.batchAckPull(ackIds, groupId);
        ackResult.messageRejectIds = persistenceMapper.batchRejectPull(rejectIds, groupId);

        return new Packet(Command.PULL_ACK_RESPONSE,
                jsonSerializationService.toJson(ackResult));
    }

    @Override
    public void reply(Object result, Connection connection) {
        doReply(connection, (Packet)result);
    }
}
