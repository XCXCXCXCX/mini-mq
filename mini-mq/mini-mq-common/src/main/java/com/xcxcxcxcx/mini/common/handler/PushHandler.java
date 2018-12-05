package com.xcxcxcxcx.mini.common.handler;

import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.topic.Topic;
import com.xcxcxcxcx.mini.common.message.entity.Push;
import com.xcxcxcxcx.mini.common.message.wrapper.PushPacketWrapper;
import com.xcxcxcxcx.mini.common.message.entity.PushResult;
import com.xcxcxcxcx.mini.common.message.wrapper.PushResultPacketWrapper;
import com.xcxcxcxcx.mini.common.topic.BrokerContext;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class PushHandler extends BaseHandler{

    @Override
    public Object doHandle(Packet packet, Connection connection) {

        //1.把消息持久化到数据库，消息状态为0
        //TODO 同步
        List<Long> successId = null;
        List<Long> failId = null;
        PushResult result = new PushResult();
        result.messageAckIds = successId;
        result.messageRejectIds = failId;
        Packet responsePacket = new Packet(Command.PUSH_RESPONSE, result);

        return responsePacket;
    }
}
