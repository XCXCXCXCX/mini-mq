package com.xcxcxcxcx.client.producer;

import com.xcxcxcxcx.client.MiniClient;
import com.xcxcxcxcx.client.config.ClientConfig;
import com.xcxcxcxcx.client.connector.channel.ClientChannelHandler;
import com.xcxcxcxcx.mini.api.client.Role;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class Producer<T> implements com.xcxcxcxcx.mini.api.client.Producer<T>{

    private final String producerId;

    private final String topicId;

    private final Role role;

    private final String key;

    private final MiniClient miniClient;

    private final ClientChannelHandler handler;

    private ClientConfig clientConfig;

    public Producer(String id, String topicId, String key, Map<String, Object> config) {
        this.producerId = id;
        this.topicId = topicId;
        this.role = new Role();
        role.setRoleName(Role.PRODUCER);
        role.setConfig(config);
        this.key = key;

        initClientConfig();

        this.miniClient = new MiniClient( clientConfig,this);
        this.handler = (ClientChannelHandler)miniClient.getHandler();
    }

    private void initClientConfig() {
        //TODO
    }

    public Producer(String id, String topicId) {
        this(id, topicId, null, null);
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

    /**
     * 异步
     * @param o
     */
    @Override
    public CompletableFuture<Boolean> send(T o) {
        //TODO
        try {
            return handler.send(o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Boolean synSendAck(Long id) {
        return sendAck(id).join();
    }

    /**
     * 异步
     * @param id
     * @return
     */
    @Override
    public CompletableFuture<Boolean> sendAck(Long id) {
        try {
            return handler.sendAck(id, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Boolean synSendReject(Long id) {
        return sendReject(id).join();
    }

    /**
     * 异步
     * @param id
     * @return
     */
    @Override
    public CompletableFuture<Boolean> sendReject(Long id) {
        try {
            return handler.sendAck(id, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

}
