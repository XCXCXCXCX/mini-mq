package com.xcxcxcxcx.mini.common.handler;

import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.common.message.entity.Ack;
import com.xcxcxcxcx.mini.common.message.entity.PullAckResult;
import com.xcxcxcxcx.mini.common.message.wrapper.BasePacketWrapper;
import com.xcxcxcxcx.mini.common.message.wrapper.PullAckPacketWrapper;


/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class PullAckHandler extends AckHandler{

    @Override
    protected int getRejectStatus() {
        return Message.MessageStatus.CONSUME_FAILED.getId();
    }

    @Override
    protected int getAckStatus() {
        return Message.MessageStatus.CONSUMER_SUCCESS.getId();
    }

    @Override
    protected int getOldStatus() {
        return Message.MessageStatus.PROCESSIGN.getId();
    }

    @Override
    protected Ack getAckRequest(Packet packet, Connection connection) {
        return new PullAckPacketWrapper(connection, packet).get();
    }

    @Override
    protected Ack getAckResponse() {
        return new PullAckResult();
    }
}
