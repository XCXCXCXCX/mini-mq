package com.xcxcxcxcx.mini.common.topic;

import com.xcxcxcxcx.mini.api.connector.topic.Topic;
import com.xcxcxcxcx.mini.api.persistence.PersistenceFactory;
import com.xcxcxcxcx.mini.api.persistence.PersistenceMapper;
import com.xcxcxcxcx.mini.api.spi.persistence.PersistenceService;
import com.xcxcxcxcx.mini.api.spi.persistence.PersistenceServiceFactory;
import com.xcxcxcxcx.mini.api.spi.persistence.TopicEntity;
import com.xcxcxcxcx.persistence.db.persistence.DbFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class BrokerContext{

    private static final DefaultTopicFactory topicFactory = new DefaultTopicFactory();

    private static final Map<String, Topic> topicMap = new ConcurrentHashMap<>();

    public static Boolean isExist(String topicId) {
        return topicMap.containsKey(topicId);
    }

    private static final PersistenceMapper persistenceMapper = DbFactory.getMapper();

    static {
        init();
    }

    private static void init(){
        List<TopicEntity> topicEntities = persistenceMapper.loadAllTopic();
        for(TopicEntity topicEntity : topicEntities){
            topicMap.put(topicEntity.getTopicId(), topicFactory.create(topicEntity.getTopicId(), topicEntity.getPartitionNum()));
        }
    }

    public static int getTopicNum() {
        return topicMap.size();
    }

    public static Boolean newTopic(String topicId, int partitionNum){
        if(partitionNum == 0){
            return false;
        }
        synchronized (topicMap){
            if(isExist(topicId)){
                return false;
            }
            try{
                persistenceMapper.createTopic(topicId, partitionNum);
                topicMap.put(topicId, topicFactory.create(topicId, partitionNum));
                return true;
            }catch (Exception e){
                return false;
            }
        }
    }

    public static Boolean removeTopic(String topicId) {
        synchronized (topicMap){
            if(!isExist(topicId)){
                return false;
            }
            try{
                persistenceMapper.removeTopic(topicId);
                topicMap.remove(topicId);
                return true;
            }catch (Exception e){
                return false;
            }
        }
    }

    public static Topic getTopicById(String topicId){
        return topicMap.get(topicId);
    }



}
