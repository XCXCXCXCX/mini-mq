package com.xcxcxcxcx.client.consumer;

import com.xcxcxcxcx.client.MiniClient;
import com.xcxcxcxcx.client.config.ClientConfig;
import com.xcxcxcxcx.client.connector.channel.ClientChannelHandler;
import com.xcxcxcxcx.mini.api.client.Role;
import com.xcxcxcxcx.mini.api.connector.message.Message;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class Consumer<T> implements com.xcxcxcxcx.mini.api.client.Consumer<T>{

    private final String consumerGroupId;
    private int idInGroup;
    private final Role role;
    private final String topicId;
    private final String key;

    private final MiniClient miniClient;

    private final ClientChannelHandler handler;

    private ClientConfig clientConfig;

    public Consumer(String consumerGroupId, String topicId) {
        this(consumerGroupId, topicId, null, null);
    }

    public Consumer(String consumerGroupId, String topicId, String key, Map<String, Object> config) {
        this.consumerGroupId = consumerGroupId;
        this.topicId = topicId;
        this.role = new Role();
        role.setRoleName(Role.CONSUMER);
        role.setConfig(config);
        this.key = key;

        initClientConfig();
        this.miniClient = new MiniClient( clientConfig,this);
        this.handler = (ClientChannelHandler) miniClient.getHandler();
    }

    private void initClientConfig() {
        //TODO
    }

    @Override
    public String getId() {
        return consumerGroupId;
    }

    @Override
    public Role getRole() {
        return role;
    }

    @Override
    public int getIdInGroup() {
        return idInGroup;
    }

    @Override
    public String getTopicId() {
        return topicId;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    public void setIdInGroup(int idInGroup) {
        this.idInGroup = idInGroup;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Consumer)){
            return false;
        }
        return (this.hashCode() == obj.hashCode())
                && (this.getIdInGroup() == ((Consumer) obj).getIdInGroup());
    }

    @Override
    public T getMessage(Class<T> t, Boolean isBlocking) {
        //把Message格式的消息转化为用户需要的对象
        //String -> Object
        try {
            Message message = handler.receive(isBlocking);
            return jsonService.parseObject(message.getContent(),t);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Boolean synAck(Long id) {
        return ack(id).join();
    }

    @Override
    public CompletableFuture<Boolean> ack(Long id) {
        try {
            return handler.receiveAck(id, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CompletableFuture<Boolean> reject(Long id) {
        try {
            return handler.receiveAck(id, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
