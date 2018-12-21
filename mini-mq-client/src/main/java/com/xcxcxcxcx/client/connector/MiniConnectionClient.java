package com.xcxcxcxcx.client.connector;

import com.xcxcxcxcx.client.connector.channel.ClientChannelHandler;
import com.xcxcxcxcx.mini.api.client.Partner;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.event.service.ContextStateListener;
import com.xcxcxcxcx.mini.api.event.service.Listener;
import com.xcxcxcxcx.network.client.NettyTcpClient;
import io.netty.channel.ChannelHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.CompletableFuture;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class MiniConnectionClient extends NettyTcpClient{

    private final ChannelHandler handler;

    public MiniConnectionClient(int port, Partner partner) {
        this(null, port, partner, null,null);
    }

    public MiniConnectionClient(String host, int port, Partner partner, GenericFutureListener listener, Listener handlerListener) {
        super(host, port, listener);
        handler = new ClientChannelHandler(partner,handlerListener);
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return handler;
    }
}
