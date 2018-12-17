package com.xcxcxcxcx.mini.common.topic;

import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.event.service.LifeCycle;
import com.xcxcxcxcx.mini.api.persistence.PersistenceMapper;
import com.xcxcxcxcx.mini.api.spi.persistence.TopicEntity;
import com.xcxcxcxcx.mini.common.topic.entity.ConsumerEntity;
import com.xcxcxcxcx.persistence.db.persistence.DbFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 持有consumerGroup
 *
 *
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class BrokerContext{

    private static final int DEFAULT_PARTITION_NUM = 5;

    private static ConsumerGroupManager consumerGroupManager;

    /**
     * topic的分区数
     */
    private static Map<String, TopicEntity> topicInfo;

    private static PersistenceMapper persistenceMapper;

    public static void init(){
        consumerGroupManager = ConsumerGroupManager.getInstance();
        topicInfo = new ConcurrentHashMap<>();
        persistenceMapper = DbFactory.getMapper();
        List<TopicEntity> topicEntities = persistenceMapper.loadAllTopic();
        for(TopicEntity topicEntity : topicEntities){
            topicInfo.put(topicEntity.getTopicId(), topicEntity);
        }
    }

    public static int getTopicNum() {
        return topicInfo.size();
    }

    public static Boolean isExist(String topicId) {
        return topicInfo.containsKey(topicId);
    }

    public static Boolean isExistGroup(String groupId) {
        return consumerGroupManager.isExist(groupId);
    }

    public static Boolean newTopic(String topicId){
        return newTopic(topicId, DEFAULT_PARTITION_NUM);
    }

    public static Boolean newTopic(String topicId, int partitionNum){
        if(partitionNum == 0){
            return false;
        }
        synchronized (topicInfo){
            if(isExist(topicId)){
                return false;
            }
            try{
                persistenceMapper.createTopic(topicId, partitionNum);
                TopicEntity topicEntity = new TopicEntity();
                topicEntity.setTopicId(topicId);
                topicEntity.setPartitionNum(partitionNum);
                topicInfo.put(topicId, topicEntity);
                return true;
            }catch (Exception e){
                return false;
            }
        }
    }

    public static Boolean removeTopic(String topicId) {
        synchronized (topicInfo){
            if(!isExist(topicId)){
                return false;
            }
            try{
                persistenceMapper.removeTopic(topicId);
                topicInfo.remove(topicId);
                return true;
            }catch (Exception e){
                return false;
            }
        }
    }

    public static TopicEntity getTopicById(String topicId){
        return topicInfo.get(topicId);
    }

    public static int getTopicPartitionNum(String topicId) {
        TopicEntity topicEntity = topicInfo.get(topicId);
        return topicEntity == null ? 0 : topicEntity.getPartitionNum();
    }

    /**
     * 如果不存在该消费组，则创建并加入
     * 如果存在该消费组，则加入
     * @param consumer
     */
    public static void joinGroup(ConsumerEntity consumer) {
        if(!consumerGroupManager.isExist(consumer.getId())){
            consumerGroupManager.createGroup(consumer);
        }else{
            consumerGroupManager.joinGroup(consumer);
        }
    }

    public static void leaveGroup(ConsumerEntity consumer){
        consumerGroupManager.leaveGroup(consumer);
    }

    /**
     * 获得该topic的当前订阅量
     * @param groupId
     * @param topicId
     * @return
     */
    public static int getSubscribeNum(String groupId, String topicId){
        return consumerGroupManager.getSubscribeNum(groupId, topicId);
    }

    /**
     * 获得该topic的当前消息量
     * @param groupId
     * @param topicId
     * @return
     */
    public static int getMessageSum(String groupId, String topicId){
        return consumerGroupManager.getMessageSum(groupId, topicId);
    }


    /**
     * 将消息推到每个存在topicId订阅者的partition中
     * @param topicId
     * @param messages
     * @return
     */
    public static void sendMessage(String topicId, List<Message> messages) {
        consumerGroupManager.sendMessage(topicId, messages);
    }

    /**
     * 将消息推到每个存在topicId订阅者的partition中
     * @param key
     * @param topicId
     * @param messages
     * @return
     */
    public static void sendMessage(String key, String topicId, List<Message> messages) {
        consumerGroupManager.sendMessage(key, topicId, messages);
    }

    public static List<Message> getMessage(String groupId, String topicId) {
        return consumerGroupManager.getMessage(groupId, topicId);
    }

    public static List<Message> getMessage(String groupId, String topicId, String key) {
        return consumerGroupManager.getMessage(groupId, topicId, key);
    }
}
