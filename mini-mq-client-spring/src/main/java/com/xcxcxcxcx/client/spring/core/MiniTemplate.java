package com.xcxcxcxcx.client.spring.core;

import com.xcxcxcxcx.client.producer.ProducerFactory;
import com.xcxcxcxcx.mini.api.client.Producer;

import java.util.concurrent.CompletableFuture;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class MiniTemplate<T> implements MiniOperations<T>{

    private final ProducerFactory<T> producerFactory;

    private final Producer<T> producer;

    public MiniTemplate(ProducerFactory<T> producerFactory) {
        this.producerFactory = producerFactory;
        producer = producerFactory.create();
    }

    /**
     * 异步发送消息（批量自动确认）
     *
     * @param key
     * @param t
     * @return
     */
    @Override
    public CompletableFuture<Boolean> send(String key, T t) {
        return producer.send(key, t);
    }

    @Override
    public CompletableFuture<Boolean> send(T t) {
        return producer.send(t);
    }

    /**
     * 同步发送消息（批量自动确认）
     *
     * @param key
     * @param t
     * @return
     */
    @Override
    public Boolean synSend(String key, T t) {
        return producer.synSend(key, t);
    }

    @Override
    public Boolean synSend(T t) {
        return producer.synSend(t);
    }
}
