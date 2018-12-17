package com.xcxcxcxcx.mini.api.client;


import java.util.concurrent.CompletableFuture;

/**
 *
 * 消费者有唯一的id
 * 由groupId-id拼接而成
 *
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface Consumer<T> extends Partner{

    /**
     * 组内id
     * @return
     */
    int getIdInGroup();

    void setIdInGroup(int idInGroup);

    /**
     * 消费消息（阻塞，非阻塞）
     * @return
     */
    T getMessage(Class<T> t, Boolean isBlocking);

    /**
     * 确认消费消息（同步）
     */
    Boolean synAck(Long id);

    /**
     * 确认消费消息（异步）
     */
    CompletableFuture<Boolean> ack(Long id);

    CompletableFuture<Boolean> reject(Long id);
}
