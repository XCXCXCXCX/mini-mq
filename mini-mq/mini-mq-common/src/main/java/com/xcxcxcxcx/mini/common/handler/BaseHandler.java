package com.xcxcxcxcx.mini.common.handler;

import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.PacketHandler;
import com.xcxcxcxcx.mini.api.spi.json.JsonSerializationService;
import com.xcxcxcxcx.mini.api.spi.json.JsonSerializationServiceFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public abstract class BaseHandler implements PacketHandler{

    private JsonSerializationService jsonSerializationService = JsonSerializationServiceFactory.create();

    private static final int MAX_RETRY = 3;

    private ThreadLocal<Integer> currentRetryTimes = new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    @Override
    public void reply(final Object result, Connection connection) {

        Packet packet = new Packet(Command.PULL_RESPONSE, jsonSerializationService.toJson(result));

        doReply(connection, packet, result);

    }

    private void doReply(Connection connection, Packet packet, Object result) {
        //返回给client
        connection.send(packet, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(!future.isSuccess()){
                    doReply(connection, packet, result);
                    currentRetryTimes.set(currentRetryTimes.get() + 1);
                    if(MAX_RETRY < currentRetryTimes.get()){
                        return;
                    }
                }
            }
        });
    }

    @Override
    public void handle(Packet packet, Connection connection) {

        //消息处理，一般是将topic下的partition消息取出来
        Object result = doHandle(packet, connection);

        //一般是响应消息处理结果给对端
        reply(result, connection);

    }

    public abstract Object doHandle(Packet packet, Connection connection);
}
