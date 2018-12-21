package com.xcxcxcxcx.client.consumer;

import com.xcxcxcxcx.client.MiniClient;
import com.xcxcxcxcx.client.config.ClientConfig;
import com.xcxcxcxcx.client.connector.channel.ClientChannelHandler;
import com.xcxcxcxcx.mini.api.client.BaseMessage;
import com.xcxcxcxcx.mini.api.client.Consumer;
import com.xcxcxcxcx.mini.api.client.Role;
import com.xcxcxcxcx.mini.api.connector.message.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class MiniConsumer<T> implements com.xcxcxcxcx.mini.api.client.Consumer<T>{

    private final String consumerGroupId;
    private int idInGroup;
    private final Role role;
    private final String topicId;
    private final String key;

    private final MiniClient miniClient;

    private final ClientChannelHandler handler;

    private final ClientConfig clientConfig;

    public MiniConsumer(ClientConfig clientConfig, String consumerGroupId, String topicId) {
        this(clientConfig, consumerGroupId, topicId, null, null);
    }

    public MiniConsumer(ClientConfig clientConfig, String consumerGroupId, String topicId, String key, Map<String, Object> config) {
        this.consumerGroupId = consumerGroupId;
        this.topicId = topicId;
        this.role = new Role();
        role.setRoleName(Role.CONSUMER);
        role.setConfig(config);
        this.key = key;
        this.clientConfig = clientConfig;
        this.miniClient = new MiniClient(clientConfig,this);
        this.handler = (ClientChannelHandler) miniClient.getChannelHandler();
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
    public BaseMessage<T> getMessage(Class<T> t, Boolean isBlocking) {
        //把Message格式的消息转化为用户需要的对象
        //String -> Object
        try {
            Message message = handler.receive(isBlocking);
            return new BaseMessage<T>().setMid(message.getMid()).setContent(t.equals(String.class)? (T)message.getContent() : jsonService.parseObject(message.getContent(),t));
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
    public Boolean synReject(Long id) {
        return reject(id).join();
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
