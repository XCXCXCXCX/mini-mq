package com.xcxcxcxcx.mini.common.message.handler;

import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.persistence.PersistenceMapper;
import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePullAckResult;
import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePullAck;
import com.xcxcxcxcx.mini.api.spi.executor.ExecutorFactory;
import com.xcxcxcxcx.mini.common.message.wrapper.SettlePullAckPacketWrapper;
import com.xcxcxcxcx.mini.common.topic.BrokerContext;
import com.xcxcxcxcx.mini.common.topic.task.RetryConsumptionTask;
import com.xcxcxcxcx.persistence.db.persistence.DbFactory;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class SettlePullAckHandler extends BaseHandler {

    private final PersistenceMapper persistenceMapper = DbFactory.getMapper();

    private final Executor executor = ExecutorFactory.create().get("Asyn-retry-pull-to-memory-executor");

    @Override
    public void reply(Object result, Connection connection) {
        doReply(connection, (Packet)result);
    }

    @Override
    public Object doHandle(Packet packet, Connection connection) {
        SettlePullAck settlePullAck = new SettlePullAckPacketWrapper(connection, packet).get();
        String key = settlePullAck.key;
        List<Long> pullAckIds = settlePullAck.ackIds;
        List<Long> pullRejectIds = settlePullAck.rejectIds;

        SettlePullAckResult result = new SettlePullAckResult();
        String groupId = connection.getSessionContext().getId();
        String topicId = connection.getSessionContext().getTopicId();
        result.ackIds = persistenceMapper.batchAckPull(pullAckIds, groupId);
        result.rejectIds = persistenceMapper.batchRejectPull(pullRejectIds, groupId);

        if(result.rejectIds != null && !result.rejectIds.isEmpty()){
            executor.execute(new RetryConsumptionTask(result.rejectIds, topicId, groupId, key, result.rejectIds.size()));
        }

        return new Packet(Command.PULL_ACK_SETTLE_RESPONSE, result);
    }
}
