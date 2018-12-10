package com.xcxcxcxcx.mini.api.client;

import com.xcxcxcxcx.mini.api.connector.message.Message;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface ConsumerGroupManager {

    void createGroup();

    void joinGroup(Consumer consumer);

    void removeGroup();

    /**
     * 广播，暂时不能保证消息被每个消费者有且仅消费一次
     * push消息
     */
    void broadcast(List<Message> messageList);
}
