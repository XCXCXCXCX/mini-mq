package com.xcxcxcxcx.client.connector.message;

import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.PacketDispatcher;
import com.xcxcxcxcx.mini.api.connector.message.PacketHandler;
import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class DefaultClientPacketDispatcher implements PacketDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClientPacketDispatcher.class);

    private final Map<Byte, PacketHandler> availableHandler;

    private final PacketHandler DEFAULT_HANDLER = new BaseHandler() {

        @Override
        public void reply(Object result, Connection connection) {

        }

        @Override
        public Object doHandle(Packet packet, Connection connection) {
            LOGGER.info("receive packet cmd={},from connection sessionId={}",
                    Command.toCMD(packet.getHeader().getCmd()), connection.getId());
            return null;
        }
    };

    public DefaultClientPacketDispatcher() {
        this.availableHandler = new ConcurrentHashMap<>();
    }

    @Override
    public void register(Command command, PacketHandler handler) {
        availableHandler.putIfAbsent(command.cmd, handler);
    }

    @Override
    public void dispatch(Packet packet, Connection connection) {

        PacketHandler handler = availableHandler.get(packet.getHeader().getCmd());

        if (handler == null) {
            DEFAULT_HANDLER.handle(packet, connection);
        } else {
            handler.handle(packet, connection);
        }

    }
}
