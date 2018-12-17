package com.xcxcxcxcx.mini.api.connector.message.handler;

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

    protected JsonSerializationService jsonSerializationService = JsonSerializationServiceFactory.create();

    private static final int MAX_RETRY = 3;

    private ThreadLocal<Integer> currentRetryTimes = new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    protected void doReply(Connection connection, Packet packet) {
        //返回给client
        connection.send(packet, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(!future.isSuccess()){
                    doReply(connection, packet);
                    currentRetryTimes.set(currentRetryTimes.get() + 1);
                    if(MAX_RETRY == currentRetryTimes.get()){
                        return;
                    }
                }
            }
        });
    }

    @Override
    public abstract void reply(Object result, Connection connection);

    @Override
    public abstract Object doHandle(Packet packet, Connection connection);
}
