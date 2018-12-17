package com.xcxcxcxcx.client.connector.message.handler;

import com.xcxcxcxcx.mini.api.client.ResponseReceiver;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.PullResult;
import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import com.xcxcxcxcx.mini.api.connector.message.wrapper.PullResultPacketWrapper;
import com.xcxcxcxcx.mini.tools.log.LogUtils;

import java.util.List;
import java.util.Queue;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PullResponseHandler extends BaseHandler {

    private final Queue<Message> pendingHandleMessage;

    private Boolean isPrefetching;

    private final ResponseReceiver responseReceiver;

    public PullResponseHandler(Queue<Message> pendingHandleMessage,final Boolean isPrefetching, ResponseReceiver responseReceiver) {
        this.pendingHandleMessage = pendingHandleMessage;
        this.isPrefetching = isPrefetching;
        this.responseReceiver = responseReceiver;
    }

    @Override
    public void reply(Object result, Connection connection) {

    }

    @Override
    public Object doHandle(Packet packet, Connection connection) {
        PullResult result = new PullResultPacketWrapper(connection, packet).get();
        List<Message> messages = result.messages;

        //1.将拉取到的消息发送到本地消息池中，供getMessage调用
        for(Message message : messages){
            if(!pendingHandleMessage.offer(message)){
                LogUtils.handler.error("message is offered to pendingHandleMessage failed, maybe Queue" +
                        "is full");
            }
        }

        isPrefetching = false;

        ResponseReceiver.Response<PullResult> response = new ResponseReceiver.Response<PullResult>()
                .set(result)
                .setResponseType(ResponseReceiver.ResponseEnum.PULL_RESPONSE.getCode());
        responseReceiver.receive(response);

        //无需响应消息
        return null;
    }
}
