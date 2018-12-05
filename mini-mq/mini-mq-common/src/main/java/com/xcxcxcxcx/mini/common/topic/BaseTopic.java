package com.xcxcxcxcx.mini.common.topic;

import com.xcxcxcxcx.mini.api.client.*;
import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.connector.topic.Topic;
import com.xcxcxcxcx.mini.api.connector.topic.router.LoadBalance;
import com.xcxcxcxcx.mini.api.spi.router.Router;
import com.xcxcxcxcx.mini.api.spi.router.RouterFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 默认topic的实现
 * @author XCXCXCXCX
 * @since 1.0
 */
public abstract class BaseTopic implements Topic{

    /**
     * 全局唯一的topicId
     */
    private final String topicId;

    /**
     * 一个topic可以有多个物理队列
     */
    private final List<BasePartition> partitions;

    private final AtomicInteger currentPartitionIndex;

    private final DefaultPartitionFactory factory = new DefaultPartitionFactory(this);

    private final Map<String, Producer> producerMap = new ConcurrentHashMap<>();
    private final Map<String, Consumer> consumerMap = new ConcurrentHashMap<>();

    public BaseTopic(String topicId, int partitionNum) {
        this.topicId = topicId;
        currentPartitionIndex = new AtomicInteger(partitionNum);
        partitions = factory.createList(partitionNum);
    }

    public BaseTopic(String topicId) {
        this(topicId, 1);
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
        return partitions.size();
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
    public Map<String, Producer> getProducers() {
        return producerMap;
    }

    @Override
    public Map<String, Consumer> getConsumers() {
        return consumerMap;
    }

    @Override
    public void join(Partner partner, Properties roleProperties) {
        String roleName = partner.getRole().getRoleName();
        if(Role.PRODUCER.equals(roleName)){
            producerMap.put(partner.getId(), (Producer)partner);

        }else if(Role.CONSUMER.equals(roleName)){
            consumerMap.put(partner.getId(), (Consumer) partner);


        }else if(Role.PRODUCER_AND_CONSUMER.equals(roleName)){
            producerMap.put(partner.getId(), (Producer)partner);
            consumerMap.put(partner.getId(), (Consumer) partner);

        }else{
            throw new IllegalArgumentException("the joining role is not exist");
        }
    }

    /**
     * 选择订阅量最多的partition来发布消息
     * @param messages
     */
    @Override
    public void sendMessage(List<Message> messages) {
        //choose Max-SubscribeNum  partition
        ((BasePartition)getRouter().route(LoadBalance.LoadBalanceStrategy.MAX_LB, partitions)).pushMessage(messages);

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
     * 选择订阅量最少的partition来消费
     * @return
     */
    @Override
    public List<Message> getMessage() {

        //choose Min-SubscribeNum  partition
        return ((BasePartition)getRouter().route(LoadBalance.LoadBalanceStrategy.MIN_LB, partitions)).pullMessage();
    }

}
