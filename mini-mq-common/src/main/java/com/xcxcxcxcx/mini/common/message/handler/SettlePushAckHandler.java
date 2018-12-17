package com.xcxcxcxcx.mini.common.message.handler;

import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePushAckResult;
import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import com.xcxcxcxcx.mini.api.persistence.PersistenceMapper;
import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePushAck;
import com.xcxcxcxcx.mini.common.message.wrapper.SettlePushAckPacketWrapper;
import com.xcxcxcxcx.persistence.db.persistence.DbFactory;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class SettlePushAckHandler extends BaseHandler {

    private PersistenceMapper persistenceMapper = DbFactory.getMapper();

    @Override
    public void reply(Object result, Connection connection) {
        doReply(connection, (Packet) result);
    }

    @Override
    public Object doHandle(Packet packet, Connection connection) {

        SettlePushAck settlePushAck = new SettlePushAckPacketWrapper(connection, packet).get();
        List<Long> pushAckIds = settlePushAck.ackIds;
        //TODO
        //从db中查询这些id的消息的status，如果这些消息已被确认（status=1），则忽略
        // 如果这些消息不是status=1，则更新其status=1
        // 并返回修改成功的status=1的消息给client

        //直接更新id in pushAckIds and status = 0的记录status = 1
        //select id from message where id in pushAckIds and status = 1
        SettlePushAckResult result = new SettlePushAckResult();
        result.ackIds = persistenceMapper.batchAckPush(pushAckIds);;

        return new Packet(Command.PUSH_ACK_SETTLE, result);
    }
}
