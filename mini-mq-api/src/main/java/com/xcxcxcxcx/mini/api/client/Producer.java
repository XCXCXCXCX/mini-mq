package com.xcxcxcxcx.mini.api.client;


import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.CompletableFuture;

/**
 *
 * 生产者
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface Producer<T> extends Partner{

    /**
     * 发送消息（同步）
     */
    Boolean synSend(T o);

    /**
     * 发送消息（异步）
     */
    CompletableFuture<Boolean> send(T o);

    /**
     * 确认发送消息（同步）
     */
    Boolean synSendAck(Long id);

    /**
     * 确认发送消息（异步）
     */
    CompletableFuture<Boolean> sendAck(Long id);

    Boolean synSendReject(Long id);

    CompletableFuture<Boolean> sendReject(Long id);
}
