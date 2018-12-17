package com.xcxcxcxcx.mini.common.topic;

import com.sun.istack.internal.Nullable;
import com.xcxcxcxcx.mini.api.client.Consumer;
import com.xcxcxcxcx.mini.api.client.Producer;
import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.connector.topic.Partition;
import com.xcxcxcxcx.mini.api.connector.topic.Topic;
import com.xcxcxcxcx.mini.tools.config.MiniConfig;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public abstract class BasePartition implements Partition{

    private static final Duration MAX_POLL_TIME = MiniConfig.mini.partition.max_poll_time;
    private static final int MAX_POLL_MESSAGE_NUM = MiniConfig.mini.partition.max_poll_message_num;


    private final String topicId;

    private final int id;

    private final Queue<Message> queue = new ConcurrentLinkedQueue<>();

    public BasePartition(String topicId, int id) {
        this.topicId = topicId;
        this.id = id;
    }

    @Override
    public Queue getQueue() {
        return queue;
    }

    @Override
    public String getTopicId() {
        return topicId;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getMessageSum() {
        return queue.size();
    }

    @Override
    public abstract void subscribe(Consumer consumer);

    @Override
    public abstract void unsubscribe(Consumer consumer);

    @Override
    public void publish(Producer producer) {

    }

    @Override
    public void unpublish(Producer producer) {

    }

    public abstract int getSubscribeNum();

    public abstract int getPublishNum();

    /**
     * 如果队列已满，则offer失败，等待下次从数据库pull后重新入队
     * @param messages
     */
    @Override
    public void pushMessage(List<Message> messages) {
        for(Message message: messages){
            queue.offer(message);
        }
    }

    @Override
    @Nullable
    public List<Message> pullMessage() {

        long start = System.currentTimeMillis();
        List<Message> messages = new ArrayList<>();
        while(true){
            Message message = queue.poll();
            if(pullCondition(start, messages.size())){
                return messages.isEmpty() ? null : messages;
            }
            messages.add(message);
        }

    }

    /**
     * 拉取结束的条件：
     * 1.拉取时间片结束
     * 2.拉取消息量达到阈值
     * @return
     */
    private Boolean pullCondition(long start,int messageSize){
        if(System.currentTimeMillis() - start > MAX_POLL_TIME.getSeconds() * 1000
                || messageSize > MAX_POLL_MESSAGE_NUM){
            return true;
        }
        return false;
    }

    public void destroy(){
        queue.clear();
    }
}
