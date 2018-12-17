package com.xcxcxcxcx.mini.common.topic;

import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.connector.topic.Partition;
import com.xcxcxcxcx.mini.api.connector.topic.Topic;
import com.xcxcxcxcx.mini.api.connector.topic.router.LoadBalance;
import com.xcxcxcxcx.mini.api.spi.router.Router;
import com.xcxcxcxcx.mini.api.spi.router.RouterFactory;
import com.xcxcxcxcx.mini.common.topic.task.RetryConsumptionTask;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 默认topic的实现
 * @author XCXCXCXCX
 * @since 1.0
 */
public abstract class BaseTopic implements Topic{

    private final static int DEFAULT_RETRY_PULL_MAX_NUM = 100;

    /**
     * 全局唯一的topicId
     */
    private final String topicId;

    private final String groupId;

    private final AtomicInteger currentPartitionIndex;

    private final DefaultPartitionFactory factory = new DefaultPartitionFactory(this);

    private final List<BasePartition> partitions;

    private final Set<String> subscribedConsumers;

    public BaseTopic(String topicId,
                     String groupId,
                     int partitionNum) {
        this.topicId = topicId;
        this.groupId = groupId;
        currentPartitionIndex = new AtomicInteger(partitionNum);
        partitions = factory.createList(partitionNum);
        subscribedConsumers = new ConcurrentSkipListSet<>();
    }

    public BaseTopic(String topicId,
                     String groupId) {
        this(topicId, groupId, 1);
    }

    public int getAndIncr(){
        return currentPartitionIndex.getAndIncrement();
    }

    @Override
    public String getId() {
        return topicId;
    }

    @Override
    public int countCurrentPartition() {
        return currentPartitionIndex.get();
    }

    @Override
    public int addPartitionAndCount() {
        partitions.add(factory.create());
        return partitions.size();
    }

    @Override
    public int removePartitionAndCount() {
        partitions.remove(currentPartitionIndex.getAndDecrement() - 1);
        return partitions.size();
    }

    @Override
    public Router getRouter() {
        return RouterFactory.create();
    }

    @Override
    public int subscribe(String consumerIdAndIdInGroup){
        subscribedConsumers.add(consumerIdAndIdInGroup);
        return subscribedConsumers.size();
    }

    @Override
    public int unsubscribe(String consumerIdAndIdInGroup){
        subscribedConsumers.remove(consumerIdAndIdInGroup);
        return subscribedConsumers.size();
    }

    @Override
    public int getSubscribeNum(){
        return subscribedConsumers.size();
    }

    @Override
    public int getMessageSum() {
        int sum = 0;
        for(Partition partition : partitions){
            sum += partition.getMessageSum();
        }
        return sum;
    }

    @Override
    public void destroy() {
        for(BasePartition partition : partitions){
            partition.destroy();
        }
    }

    @Override
    public void sendMessage(Message message) {
        //choose partition randomly
        ((BasePartition)getRouter().route(LoadBalance.LoadBalanceStrategy.RANDOM, partitions)).pushMessage(Collections.singletonList(message));
    }

    @Override
    public void sendMessage(Message message, String key) {
        sendMessage(Collections.singletonList(message), key);
    }

    /**
     *
     * 随机选择partition来发布消息
     * @param messages
     */
    @Override
    public void sendMessage(List<Message> messages) {
        for(Message message : messages){
            String key = message.getKey();
            if(key == null){
                sendMessage(message, key);
            }else{
                sendMessage(message);
            }
        }
    }

    /**
     * 根据key来路由到某一个partition
     * @param key
     * @return
     */
    @Override
    public void sendMessage(List<Message> messages, String key) {
        ((BasePartition)getRouter().route(partitions, key)).pushMessage(messages);
    }

    /**
     * 根据key来路由到某一个partition
     * @param key
     * @return
     */
    @Override
    public List<Message> getMessage(String key) {

        return ((BasePartition)getRouter().route(partitions, key)).pullMessage();
    }

    /**
     * 随机选择partition来消费
     * @return
     */
    @Override
    public List<Message> getMessage() {
        //choose partition randomly
        return ((BasePartition)getRouter().route(LoadBalance.LoadBalanceStrategy.RANDOM, partitions)).pullMessage();
    }

}
