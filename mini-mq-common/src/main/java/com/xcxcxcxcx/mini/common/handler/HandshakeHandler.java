package com.xcxcxcxcx.mini.common.handler;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.PacketHandler;

/**
 *
 * 三次握手，建立可靠的连接并填充SessionContext信息
 * 客户端发送信息：applicationName、sessionId、partner（其中包括消费者或生产者配置）
 *              并保存当前session状态为connecting
 * 服务端接受消息：查看当前持有session中是否存在该sessionId
 *        1. 如果存在，覆盖配置，并修改该session状态为connecting
 *        2. 如果不存在，将sessionContext保存到当前持有session集合中，并修改该session状态为connecting
 * 服务端响应消息：handshakeOK
 * 客户端接收消息：handshakeOK，更改当前session为connected
 * 客户端响应消息：handshakeOK，发送给服务端，客户端三次握手结束
 * 服务端接收消息：handshakeOK，更改该session状态为connected，服务端三次握手结束
 * @author XCXCXCXCX
 * @since 1.0
 */
public class HandshakeHandler implements PacketHandler{


    @Override
    public void reply(Object result, Connection connection) {

    }

    @Override
    public Object doHandle(Packet packet, Connection connection) {
        return null;
    }
}
