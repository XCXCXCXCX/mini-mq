package com.xcxcxcxcx.mini.common.topic;


import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.common.topic.entity.ConsumerEntity;
import com.xcxcxcxcx.mini.tools.thread.ThreadPoolManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ConsumerGroupManager {

    private static final ConsumerGroupManager instance = new ConsumerGroupManager();

    private final Map<String, ConsumerGroup> consumerGroupMap = new ConcurrentHashMap<>();

    public static ConsumerGroupManager getInstance(){
        return instance;
    }


    /**
     * 创建消费组
     *
     * @param consumer
     * @return idInGroup
     */
    public void createGroup(ConsumerEntity consumer) {
        consumerGroupMap.putIfAbsent(consumer.getId(),
                new ConsumerGroup(consumer,
                        BrokerContext.getTopicPartitionNum(consumer.getTopicId())));
    }

    /**
     * 返回0表示加入失败
     * @param consumer
     * @return
     */
    public void joinGroup(ConsumerEntity consumer) {
        ConsumerGroup group = consumerGroupMap.get(consumer.getId());
        if(group == null){
            throw new RuntimeException("当前BrokerContext不存在该消费组 : groupId = " + consumer.getId());
        }else{
            group.joinGroup(consumer);
        }
    }

    /**
     * 离开消费组
     */
    public void leaveGroup(ConsumerEntity consumer){

        ConsumerGroup group = consumerGroupMap.get(consumer.getId());
        if(group == null){
            throw new RuntimeException("当前BrokerContext不存在该消费组 : groupId = " + consumer.getId());
        }else{
            group.leaveGroup(consumer);
        }

    }

    /**
     * 是否已存在该消费组
     *
     * @param groupId
     * @return
     */
    public Boolean isExist(String groupId) {
        return consumerGroupMap.containsKey(groupId);
    }

    /**
     * 根据groupId和topicId定位到topic的当前订阅量
     * @param groupId
     * @param topicId
     * @return
     */
    public int getSubscribeNum(String groupId, String topicId){
        ConsumerGroup group = consumerGroupMap.get(groupId);
        if(group == null){
            throw new RuntimeException("当前BrokerContext不存在该消费组 : groupId = " + groupId);
        }
        return group.getSubscribeNum(topicId);
    }

    public int getMessageSum(String groupId, String topicId) {
        ConsumerGroup group = consumerGroupMap.get(groupId);
        if(group == null){
            throw new RuntimeException("当前BrokerContext不存在该消费组 : groupId = " + groupId);
        }
        return group.getMessageSum(topicId);
    }

    /**
     * 根据消费组id和topicId拉取消息
     *
     * @return
     */
    public List<Message> getMessage(String groupId, String topicId){
        ConsumerGroup group = consumerGroupMap.get(groupId);
        if(group == null){
            throw new RuntimeException("该消费组还没有被创建! : " + groupId);
        }else{
            return group.getMessage(topicId);
        }
    }

    /**
     * 根据消费组id和topicId拉取消息
     *
     * @return
     */
    public List<Message> getMessage(String groupId, String topicId, String key){
        ConsumerGroup group = consumerGroupMap.get(groupId);
        if(group == null){
            throw new RuntimeException("该消费组还没有被创建! : " + groupId);
        }else{
            return group.getMessage(topicId, key);
        }
    }


    public void sendMessage(String topicId, List<Message> messages) {
        if(messages == null || messages.isEmpty()){
            return;
        }
        for(Map.Entry<String, ConsumerGroup> entry : consumerGroupMap.entrySet()){
            doSendMessageToEveryGroup(entry.getValue(), topicId, messages, null);
        }
    }


    public void sendMessage(String key, String topicId, List<Message> messages) {
        if(messages == null || messages.isEmpty()){
            return;
        }
        for(Map.Entry<String, ConsumerGroup> entry : consumerGroupMap.entrySet()){
            doSendMessageToEveryGroup(entry.getValue(), topicId, messages, key);
        }
    }

    private void doSendMessageToEveryGroup(ConsumerGroup group,
                                            String topicId,
                                            List<Message> messages,
                                            String key) {
        if(key == null){
            group.sendMessage(topicId, messages);
        }else{
            group.sendMessage(topicId, messages, key);
        }
    }
}
