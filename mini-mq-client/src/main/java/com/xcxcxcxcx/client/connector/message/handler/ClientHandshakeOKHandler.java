package com.xcxcxcxcx.client.connector.message.handler;

import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.HandshakeOK;
import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import com.xcxcxcxcx.mini.api.connector.message.wrapper.HandshakeOKPacketWrapper;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ClientHandshakeOKHandler extends BaseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandshakeOKHandler.class);

    @Override
    public void reply(Object result, Connection connection) {
        connection.send((Packet) result, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    LOGGER.info("second handshake success : sessionId = " + connection.getSessionContext().getSessionId());
                }
            }
        });
    }

    @Override
    public Object doHandle(Packet packet, Connection connection) {
        HandshakeOK handshakeOK = new HandshakeOKPacketWrapper(connection, packet).get();
        connection.getSessionContext().setSessionId(handshakeOK.sessionId);

        return new Packet(Command.HAND_SHAKE_OK, handshakeOK);
    }

}
