package com.xcxcxcxcx.mini.common.topic;

import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.common.topic.entity.ConsumerEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 消费者组
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ConsumerGroup {

    private final String groupId;

    private List<ConsumerEntity> consumers;

    private AtomicInteger currentConsumerNum = new AtomicInteger(0);

    private final DefaultTopicFactory factory;

    /**
     * 一个topic有多个物理队列
     * key: topicId
     * value: partitions
     */
    private Map<String, BaseTopic> topicMaps;

    /**
     * 新建消费组
     * @param consumer
     * @param partitionNum
     */
    public ConsumerGroup(ConsumerEntity consumer, int partitionNum) {
        this.groupId = consumer.getId();
        this.consumers = new CopyOnWriteArrayList<>();
        factory = new DefaultTopicFactory();
        topicMaps = new ConcurrentHashMap<>();

        consumer.setIdInGroup(currentConsumerNum.incrementAndGet());

        consumers.add(consumer);

        String topicId = consumer.getTopicId();
        topicMaps.put(topicId, factory.create(topicId, groupId, partitionNum));

    }

    /**
     * 加入消费组
     * @param consumer
     */
    public void joinGroup(ConsumerEntity consumer){
        String topicId = consumer.getTopicId();
        BaseTopic topic = topicMaps.get(topicId);

        if(topic == null){
            topic = factory.create(topicId,groupId,
                    BrokerContext.getTopicPartitionNum(topicId));
            topicMaps.put(topicId, topic);
        }
        topic.subscribe(consumer.getId()+"-"+consumer.getIdInGroup());

        consumer.setIdInGroup(currentConsumerNum.incrementAndGet());
        consumers.add(consumer);

    }

    /**
     * 离开消费组
     * @param consumer
     */
    public void leaveGroup(ConsumerEntity consumer){
        String topicId = consumer.getTopicId();
        BaseTopic topic = topicMaps.get(topicId);

        if(topic != null){
            if(topic.unsubscribe(consumer.getId()+"-"+consumer.getIdInGroup()) == 0){
                destroy(topic);
            }
        }
        consumers.remove(consumer);
        currentConsumerNum.decrementAndGet();
    }

    /**
     * destroy无人订阅的partitions
     * @param topic
     */
    private void destroy(BaseTopic topic) {
        if(topic != null && topic.countCurrentPartition() > 0){
            topic.destroy();
        }
    }

    /**
     * 消费组的当前消费者数量
     * @return
     */
    public int getConsumerNum(){
        return currentConsumerNum.get();
    }

    /**
     * 根据topicId获得当前消费组的该topic订阅情况
     */
    public int getSubscribeNum(String topicId){
        BaseTopic topic = topicMaps.get(topicId);
        return topic == null ? 0 : topic.getSubscribeNum();
    }

    public int getMessageSum(String topicId) {
        BaseTopic topic = topicMaps.get(topicId);
        return topic == null ? 0 : topic.getMessageSum();
    }

    /**
     * 根据topicId拉取消息
     * @param topicId
     * @return
     */
    public List<Message> getMessage(String topicId) {
        BaseTopic topic = topicMaps.get(groupId);
        if(topic == null){
            throw new RuntimeException("该消费组中的topic还没有被创建! : groupId = " + groupId
                    +",topicId = " + topicId);
        }else{
            return topic.getMessage();
        }
    }

    /**
     * 根据topicId和key拉取固定分区的消息
     * @param topicId
     * @return
     */
    public List<Message> getMessage(String topicId, String key) {
        BaseTopic topic = topicMaps.get(groupId);
        if(topic == null){
            throw new RuntimeException("该消费组中的topic还没有被创建! : groupId = " + groupId
                    +",topicId = " + topicId);
        }else{
            return topic.getMessage(key);
        }
    }

    public void sendMessage(String topicId, List<Message> messages) {
        BaseTopic topic = topicMaps.get(groupId);
        if(topic == null){
            throw new RuntimeException("该消费组中的topic还没有被创建! : groupId = " + groupId
                    +",topicId = " + topicId);
        }else{
            topic.sendMessage(messages);
        }
    }

    public void sendMessage(String topicId, List<Message> messages, String key) {
        BaseTopic topic = topicMaps.get(groupId);
        if(topic == null){
            throw new RuntimeException("该消费组中的topic还没有被创建! : groupId = " + groupId
                    +",topicId = " + topicId);
        }else{
            topic.sendMessage(messages, key);
        }
    }
}
