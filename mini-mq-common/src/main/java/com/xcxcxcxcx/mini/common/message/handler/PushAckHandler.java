package com.xcxcxcxcx.mini.common.message.handler;

import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.PushAckResult;
import com.xcxcxcxcx.mini.api.connector.message.entity.PushAck;
import com.xcxcxcxcx.mini.common.message.wrapper.PushAckPacketWrapper;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PushAckHandler extends AckHandler{

    @Override
    public Object doHandle(Packet packet, Connection connection) {

        PushAck pushAck = new PushAckPacketWrapper(connection, packet).get();
        List<Long> ackIds = pushAck.messageAckIds;
        List<Long> rejectIds = pushAck.messageRejectIds;


        PushAckResult ackResult = new PushAckResult();
        ackResult.id = pushAck.id;
        ackResult.messageAckIds = persistenceMapper.batchAckPush(ackIds);
        ackResult.messageRejectIds = persistenceMapper.batchRejectPush(rejectIds);

        return new Packet(Command.PUSH_ACK_RESPONSE, ackResult);
    }

    @Override
    public void reply(Object result, Connection connection) {

        doReply(connection, (Packet)result);
    }
}
