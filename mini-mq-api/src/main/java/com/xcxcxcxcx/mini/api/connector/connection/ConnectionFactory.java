package com.xcxcxcxcx.mini.api.connector.connection;

import io.netty.channel.Channel;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface ConnectionFactory {

    Connection create(Channel channel);
}
