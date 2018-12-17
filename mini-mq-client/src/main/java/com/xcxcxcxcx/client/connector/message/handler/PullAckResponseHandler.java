package com.xcxcxcxcx.client.connector.message.handler;

import com.xcxcxcxcx.mini.api.client.ResponseReceiver;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.PullAckResult;
import com.xcxcxcxcx.mini.api.connector.message.wrapper.PullAckResultPacketWrapper;
import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PullAckResponseHandler extends BaseHandler {

    private final ResponseReceiver responseReceiver;

    public PullAckResponseHandler(ResponseReceiver responseReceiver) {
        this.responseReceiver = responseReceiver;
    }

    @Override
    public void reply(Object result, Connection connection) {

    }

    @Override
    public Object doHandle(Packet packet, Connection connection) {

        PullAckResult ack = new PullAckResultPacketWrapper(connection, packet).get();

        ResponseReceiver.Response<PullAckResult> response = new ResponseReceiver.Response<PullAckResult>()
                .setId(ack.id)
                .set(ack)
                .setResponseType(ResponseReceiver.ResponseEnum.PULL_ACK_RESPONSE.getCode());
        responseReceiver.receive(response);

        //无需响应消息
        return null;
    }
}
