package com.xcxcxcxcx.client.spring.core;

import java.util.concurrent.CompletableFuture;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface MiniOperations<T> {

    /**
     * 异步发送消息（批量自动确认）
     * @param key
     * @param t
     * @return
     */
    CompletableFuture<Boolean> send(String key, T t);

    CompletableFuture<Boolean> send(T t);

    /**
     * 同步发送消息（批量自动确认）
     * @param key
     * @param t
     * @return
     */
    Boolean synSend(String key, T t);

    Boolean synSend(T t);
}
