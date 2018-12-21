package com.xcxcxcxcx.mini.common.message.handler;

import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.PullAckResult;
import com.xcxcxcxcx.mini.api.connector.message.entity.PullAck;
import com.xcxcxcxcx.mini.api.spi.executor.ExecutorFactory;
import com.xcxcxcxcx.mini.common.message.wrapper.PullAckPacketWrapper;
import com.xcxcxcxcx.mini.common.topic.task.RetryConsumptionTask;

import java.util.List;
import java.util.concurrent.Executor;


/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PullAckHandler extends AckHandler{

    private final Executor executor = ExecutorFactory.create().get("Asyn-retry-pull-to-memory-executor");

    @Override
    public Object doHandle(Packet packet, Connection connection) {

        PullAck pullAck = new PullAckPacketWrapper(connection, packet).get();
        List<Long> ackIds = pullAck.messageAckIds;
        List<Long> rejectIds = pullAck.messageRejectIds;
        String topicId = connection.getSessionContext().getTopicId();
        String groupId = connection.getSessionContext().getId();
        String key = pullAck.key;

        PullAckResult ackResult = new PullAckResult();
        ackResult.id = pullAck.id;
        ackResult.messageAckIds = persistenceMapper.batchAckPull(ackIds, groupId);
        ackResult.messageRejectIds = persistenceMapper.batchRejectPull(rejectIds, groupId);
        if(ackResult.messageRejectIds != null && !ackResult.messageRejectIds.isEmpty()){
            executor.execute(new RetryConsumptionTask(ackResult.messageRejectIds, topicId, groupId, key, ackResult.messageRejectIds.size()));
        }
        return new Packet(Command.PULL_ACK_RESPONSE,
                jsonSerializationService.toJson(ackResult));
    }

    @Override
    public void reply(Object result, Connection connection) {
        doReply(connection, (Packet)result);
    }
}
