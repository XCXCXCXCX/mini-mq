package com.xcxcxcxcx.client.connector.message.handler;

import com.xcxcxcxcx.mini.api.client.ResponseReceiver;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.PushAckResult;
import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import com.xcxcxcxcx.mini.api.connector.message.wrapper.PushAckResultPacketWrapper;


/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PushAckResponseHandler extends BaseHandler {

    private final ResponseReceiver responseReceiver;

    public PushAckResponseHandler(ResponseReceiver responseReceiver) {
        this.responseReceiver = responseReceiver;
    }

    @Override
    public void reply(Object result, Connection connection) {

    }

    @Override
    public Object doHandle(Packet packet, Connection connection) {
        PushAckResult ack = new PushAckResultPacketWrapper(connection, packet).get();

        ResponseReceiver.Response<PushAckResult> response = new ResponseReceiver.Response<PushAckResult>()
                .setId(ack.id)
                .set(ack)
                .setResponseType(ResponseReceiver.ResponseEnum.PUSH_ACK_RESPONSE.getCode());
        responseReceiver.receive(response);

        //无需响应消息
        return null;
    }
}
