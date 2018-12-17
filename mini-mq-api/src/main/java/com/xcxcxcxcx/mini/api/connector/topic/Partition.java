package com.xcxcxcxcx.mini.api.connector.topic;

import com.xcxcxcxcx.mini.api.client.Consumer;
import com.xcxcxcxcx.mini.api.client.Producer;
import com.xcxcxcxcx.mini.api.connector.message.Message;

import java.util.List;
import java.util.Queue;

/**
 *
 * 消息的物理分区
 * @author XCXCXCXCX
 * @Since 1.0
 */
public interface Partition {

    /**
     * 每个partition持有一个阻塞队列
     * @return
     */
    Queue getQueue();

    String getTopicId();

    /**
     * 每个partition在topic中有一个唯一的id
     * @return
     */
    int getId();

    int getMessageSum();

    /**
     * 订阅和取消订阅
     * @param consumer
     */
    void subscribe(Consumer consumer);
    void unsubscribe(Consumer consumer);

    /**
     * 发布和取消发布
     * @param producer
     */
    void publish(Producer producer);
    void unpublish(Producer producer);

    /**
     * 当前订阅量和发布量
     * @return
     */
    int getSubscribeNum();
    int getPublishNum();

    /**
     * push message到队列中
     * @return 是否成功
     */
    void pushMessage(List<Message> messages);

    /**
     * 从队列尾pull message
     * @return 是否成功
     */
    List<Message> pullMessage();

}
