package com.xcxcxcxcx.client.connector;

import com.xcxcxcxcx.client.connector.channel.ClientChannelHandler;
import com.xcxcxcxcx.mini.api.client.Partner;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.network.client.NettyTcpClient;
import io.netty.channel.ChannelHandler;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class MiniConnectionClient extends NettyTcpClient{

    private final ChannelHandler handler;

    private Connection connection;

    public MiniConnectionClient(int port, Partner partner) {
        this(null, port, partner);
    }

    public MiniConnectionClient(String host, int port, Partner partner) {
        super(host, port);
        handler = new ClientChannelHandler(partner);
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return handler;
    }
}
