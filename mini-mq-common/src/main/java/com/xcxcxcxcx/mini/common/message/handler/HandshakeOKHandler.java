package com.xcxcxcxcx.mini.common.message.handler;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.PacketHandler;
import com.xcxcxcxcx.mini.api.connector.message.entity.HandshakeOK;
import com.xcxcxcxcx.mini.api.connector.message.wrapper.HandshakeOKPacketWrapper;
import com.xcxcxcxcx.mini.api.connector.session.SessionManager;
import com.xcxcxcxcx.mini.tools.log.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class HandshakeOKHandler implements PacketHandler{

    private static final Logger LOGGER = LoggerFactory.getLogger(HandshakeOKHandler.class);

    private final SessionManager sessionManager;

    public HandshakeOKHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(Packet packet, Connection connection) {
        HandshakeOK handshakeOK = new HandshakeOKPacketWrapper(connection, packet).get();
        int sessionId = handshakeOK.sessionId;
        try {
            sessionManager.openSession(sessionId);

            LOGGER.info("third handshake success : sessionId={}", sessionId);
        }catch (IllegalStateException e){
            LogUtils.handler.error("third handshake error : sessionId={}", sessionId);
        }
    }

    @Override
    public void reply(Object result, Connection connection) {

    }

    @Override
    public Object doHandle(Packet packet, Connection connection) {
        return null;
    }
}
