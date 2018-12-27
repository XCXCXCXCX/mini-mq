package com.xcxcxcxcx.client.producer;

import com.xcxcxcxcx.client.MiniClient;
import com.xcxcxcxcx.client.config.ClientConfig;
import com.xcxcxcxcx.client.connector.channel.ClientChannelHandler;
import com.xcxcxcxcx.mini.api.client.Producer;
import com.xcxcxcxcx.mini.api.client.Role;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class MiniProducer<T> implements Producer<T>{

    private final String producerId;

    private final String topicId;

    private final Role role;

    private final String key;

    private final MiniClient miniClient;

    private final ClientChannelHandler handler;

    private final ClientConfig clientConfig;

    public MiniProducer(ClientConfig clientConfig,String id, String topicId) {
        this(clientConfig, id, topicId, null, null);
    }

    public MiniProducer(ClientConfig clientConfig,
                        String id,
                        String topicId,
                        String key,
                        Map<String, Object> config) {
        this.producerId = id;
        this.topicId = topicId;
        this.role = new Role();
        role.setRoleName(Role.PRODUCER);
        role.setConfig(config);
        this.key = key;

        this.clientConfig = clientConfig;

        this.miniClient = new MiniClient(clientConfig,this);
        this.handler = (ClientChannelHandler)miniClient.getChannelHandler();
    }

    @Override
    public String getId() {
        return producerId;
    }

    @Override
    public String getTopicId() {
        return topicId;
    }

    @Override
    public Role getRole() {
        return role;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Boolean synSend(T o) {
        return send(o).join();
    }

    @Override
    public Boolean synSend(String key, T o) {
        return send(key, o).join();
    }

    /**
     * 异步
     * @param o
     */
    @Override
    public CompletableFuture<Boolean> send(T o) {
        return send(null, o);
    }

    @Override
    public CompletableFuture<Boolean> send(String key, T o){
        //TODO
        try {
            return handler.send(key, o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

}
