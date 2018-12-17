package com.xcxcxcxcx.mini.common.message;

import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.PacketDispatcher;
import com.xcxcxcxcx.mini.api.connector.message.PacketHandler;
import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import com.xcxcxcxcx.mini.tools.log.LogUtils;
import com.xcxcxcxcx.mini.tools.monitor.cost.CostUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 分发器，分发给不同的handler处理
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class DefaultPacketDispatcher implements PacketDispatcher{

    private final Map<Byte, PacketHandler> availableHandler;

    private final PacketHandler DEFAULT_HANDLER = new BaseHandler() {

        @Override
        public void reply(Object result, Connection connection) {

        }

        @Override
        public Object doHandle(Packet packet, Connection connection) {
            LogUtils.console.info("receive packet cmd={},from connection sessionId={}",
                    Command.toCMD(packet.getHeader().getCmd()), connection.getId());
            return null;
        }
    };

    public DefaultPacketDispatcher() {
        this.availableHandler = new ConcurrentHashMap<>();
    }

    @Override
    public void register(Command command, PacketHandler handler) {
        availableHandler.putIfAbsent(command.cmd, handler);
    }

    @Override
    public void dispatch(Packet packet, Connection connection) {

        PacketHandler handler = availableHandler.get(packet.getHeader().getCmd());

        try{
            CostUtils.begin("packet dispatch and handle");
            if(handler == null){
                DEFAULT_HANDLER.handle(packet, connection);
            }else{
                try{
                    CostUtils.begin("handler= " + handler.getClass().getName() + " packet handle");
                    handler.handle(packet, connection);
                }catch (Exception e){
                    throw e;
                }finally {
                    CostUtils.end();
                }
            }
        }catch (Exception e){
            LogUtils.handler.info("packet handle error, cmd={},exception={}",
                    Command.toCMD(packet.getHeader().getCmd()), e);
        }finally {
            CostUtils.end();
        }

    }
}
