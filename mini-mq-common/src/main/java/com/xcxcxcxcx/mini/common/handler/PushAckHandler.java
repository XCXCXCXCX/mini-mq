package com.xcxcxcxcx.mini.common.handler;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.common.message.entity.Ack;
import com.xcxcxcxcx.mini.common.message.entity.PushAck;
import com.xcxcxcxcx.mini.common.message.wrapper.PushAckPacketWrapper;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class PushAckHandler extends AckHandler{

    /**
     * -1，表示直接删除
     * @return
     */
    @Override
    protected int getRejectStatus() {
        return -1;
    }

    @Override
    protected int getAckStatus() {
        return Message.MessageStatus.NEW_ACK.getId();
    }

    @Override
    protected int getOldStatus() {
        return Message.MessageStatus.NEW.getId();
    }

    @Override
    protected Ack getAckRequest(Packet packet, Connection connection) {
        return new PushAckPacketWrapper(connection, packet).get();
    }

    @Override
    protected Ack getAckResponse() {
        return new PushAck();
    }
}
