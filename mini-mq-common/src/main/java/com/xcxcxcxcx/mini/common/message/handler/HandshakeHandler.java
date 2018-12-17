package com.xcxcxcxcx.mini.common.message.handler;


import com.xcxcxcxcx.mini.api.client.Role;
import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.PacketHandler;
import com.xcxcxcxcx.mini.api.connector.message.entity.Handshake;
import com.xcxcxcxcx.mini.api.connector.message.entity.HandshakeOK;
import com.xcxcxcxcx.mini.api.connector.message.wrapper.HandshakePacketWrapper;
import com.xcxcxcxcx.mini.api.connector.session.SessionContext;
import com.xcxcxcxcx.mini.api.connector.session.SessionManager;
import com.xcxcxcxcx.mini.common.topic.BrokerContext;
import com.xcxcxcxcx.mini.common.topic.entity.ConsumerEntity;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 三次握手，建立可靠的连接并填充SessionContext信息
 * 客户端发送信息：id,topicId,roleName  角色ID，角色订阅的topicId，角色类型
 *              并保存当前session状态为connecting
 * 服务端接受消息：查看当前持有session中是否存在该sessionId，保存角色信息到SessionContext
 *        1. 如果存在，覆盖配置
 *        2. 如果不存在，将sessionContext保存到当前持有session集合中
 *
 * 服务端响应消息：handshakeOK
 * 客户端接收消息：handshakeOK，更改当前session为connected，并保存服务端传递来的信息
 * 客户端响应消息：handshakeOK，发送给服务端，客户端三次握手结束
 * 服务端接收消息：handshakeOK，更改该session状态为connected，服务端三次握手结束
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class HandshakeHandler implements PacketHandler{


    private static final Logger LOGGER = LoggerFactory.getLogger(HandshakeHandler.class);

    private final SessionManager sessionManager;


    public HandshakeHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * 第一次握手
     *
     * @param packet
     * @param connection
     * @return
     */
    @Override
    public void handle(Packet packet, Connection connection) {
        Handshake handshake = new HandshakePacketWrapper(connection, packet).get();

        String id = handshake.id;
        String topicId = handshake.topicId;
        String roleName = handshake.roleName;
        SessionContext context = connection.getSessionContext();
        HandshakeOK handshakeOK = new HandshakeOK();
        handshakeOK.sessionId = context.getSessionId();
        Packet replyPacket = new Packet(Command.HAND_SHAKE_OK, handshakeOK);

        connection.send(replyPacket, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    context.setId(id);
                    context.setTopicId(topicId);
                    context.setRoleName(roleName);
                    if(Role.CONSUMER.equals(roleName)){
                        //新建topic，如果存在，无需覆盖
                        BrokerContext.newTopic(topicId);
                        //如果该topicId当前不存在本消费组, 则创建一个新的消费组, 并让该消费者加入到该消费组
                        //如果存在本消费组，则无需创建，直接加入到该消费组
                        ConsumerEntity consumer = ConsumerEntity
                                .build()
                                .setId(id)
                                .setTopicId(topicId);
                        BrokerContext.joinGroup(consumer);
                        context.setIdInGroup(consumer.getIdInGroup());
                    }

                    LOGGER.info("first handshake success : sessionId={}", context.getSessionId());
                }
            }
        });

    }

    @Override
    public void reply(Object result, Connection connection) {

    }

    @Override
    public Object doHandle(Packet packet, Connection connection) {
        return null;
    }
}
