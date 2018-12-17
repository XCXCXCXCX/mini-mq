package com.xcxcxcxcx.mini.common.message.handler;

import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.persistence.PersistenceMapper;
import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePullAckResult;
import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePullAck;
import com.xcxcxcxcx.mini.common.message.wrapper.SettlePullAckPacketWrapper;
import com.xcxcxcxcx.mini.common.topic.BrokerContext;
import com.xcxcxcxcx.persistence.db.persistence.DbFactory;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class SettlePullAckHandler extends BaseHandler {

    private PersistenceMapper persistenceMapper = DbFactory.getMapper();

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

        List<Message> messages = persistenceMapper.prePull(topicId, groupId, key, 1, Integer.MAX_VALUE);

        if(key == null){
            BrokerContext.sendMessage(topicId, messages);
        }else{
            BrokerContext.sendMessage(key, topicId, messages);
        }

        return new Packet(Command.PULL_ACK_SETTLE_RESPONSE, result);
    }
}
