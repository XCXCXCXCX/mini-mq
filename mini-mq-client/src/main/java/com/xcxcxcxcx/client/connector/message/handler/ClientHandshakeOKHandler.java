package com.xcxcxcxcx.client.connector.message.handler;

import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.HandshakeOKListener;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.HandshakeOK;
import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import com.xcxcxcxcx.mini.api.connector.message.wrapper.HandshakeOKPacketWrapper;
import com.xcxcxcxcx.mini.api.event.service.Listener;
import com.xcxcxcxcx.mini.tools.thread.ThreadPoolManager;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ClientHandshakeOKHandler extends BaseHandler{

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandshakeOKHandler.class);

    private static final Timer heartbeat_timer = new HashedWheelTimer(new ThreadPoolManager("client-heartbeat"));

    private static final int MAX_TIMEOUT_TIMES = 2;

    private static int timeoutTimes = 0;

    private final Listener reconnectListener;

    private final HandshakeOKListener handshakeOKListener;

    public ClientHandshakeOKHandler(HandshakeOKListener handshakeOKListener,
                                    Listener reconnectListener) {
        this.handshakeOKListener = handshakeOKListener;
        this.reconnectListener = reconnectListener;
    }

    @Override
    public void reply(Object result, Connection connection) {
        connection.send((Packet) result, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    handshakeOKListener.notifyHandshakeOK();
                    LOGGER.info("second handshake success : sessionId = " + connection.getSessionContext().getSessionId());
                    /**
                     * 开启心跳检测
                     */
                    startHeartBeat(connection);
                }
            }
        });
    }

    private void startHeartBeat(final Connection connection) {
        heartbeat_timer.newTimeout(new HeartbeatTask(connection),
                connection.getSessionContext().getHeartbeat(),
                TimeUnit.MILLISECONDS);
    }

    private class HeartbeatTask implements TimerTask{

        private final Connection connection;

        public HeartbeatTask(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run(Timeout timeout) throws Exception {

            if (connection == null || !connection.isConnected()) {
                LOGGER.info("heartbeat timeout times={},but connection disconnected, conn={}", timeoutTimes, connection);
                /**
                 * 触发重连
                 */
                reconnectListener.onFailure(new RuntimeException("connection unusually disconnected, try reconnect..."));
                return;
            }

            if(connection.isReadTimeout()){
                //心跳次数超过限制，关闭连接
                if(++timeoutTimes > MAX_TIMEOUT_TIMES){
                    connection.close();
                    LOGGER.info("heartbeat timeout times={}, connection disconnected, conn={}", timeoutTimes, connection);
                    return;
                }else{
                    //心跳超时但未超过限制
                    LOGGER.info("heartbeat timeout times={}, connection connected, conn={}", timeoutTimes, connection);
                }
            }else{
                timeoutTimes = 0;
            }

            if (connection.isWriteTimeout()) {
                LOGGER.info("send heartbeat ping...");
                connection.send(Packet.heartbeat);
            }

            heartbeat_timer.newTimeout(this, connection.getSessionContext().getHeartbeat(), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public Object doHandle(Packet packet, Connection connection) {
        HandshakeOK handshakeOK = new HandshakeOKPacketWrapper(connection, packet).get();
        connection.getSessionContext().setSessionId(handshakeOK.sessionId);

        return new Packet(Command.HAND_SHAKE_OK, handshakeOK);
    }

}
