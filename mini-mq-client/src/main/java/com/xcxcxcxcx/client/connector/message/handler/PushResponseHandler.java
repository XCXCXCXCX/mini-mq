package com.xcxcxcxcx.client.connector.message.handler;

import com.xcxcxcxcx.mini.api.client.ResponseReceiver;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.PushResult;
import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import com.xcxcxcxcx.mini.api.connector.message.wrapper.PushResultPacketWrapper;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PushResponseHandler extends BaseHandler {

    private final ResponseReceiver responseReceiver;

    public PushResponseHandler(ResponseReceiver responseReceiver) {
        this.responseReceiver = responseReceiver;
    }

    @Override
    public void reply(Object result, Connection connection) {

    }

    @Override
    public Object doHandle(Packet packet, Connection connection) {
        PushResult ack = new PushResultPacketWrapper(connection, packet).get();

        ResponseReceiver.Response<PushResult> response = new ResponseReceiver.Response<PushResult>()
                .setId(ack.id)
                .set(ack)
                .setResponseType(ResponseReceiver.ResponseEnum.PUSH_RESPONSE.getCode());

        responseReceiver.receive(response);

        //无需响应消息
        return null;
    }
}
