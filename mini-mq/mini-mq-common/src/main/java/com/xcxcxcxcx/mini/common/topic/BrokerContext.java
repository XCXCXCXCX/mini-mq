package com.xcxcxcxcx.mini.common.topic;

import com.xcxcxcxcx.mini.api.connector.topic.Topic;
import com.xcxcxcxcx.mini.api.spi.persistence.PersistenceService;
import com.xcxcxcxcx.mini.api.spi.persistence.PersistenceServiceFactory;

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

    private static final PersistenceService dbService = PersistenceServiceFactory.create();

    public static int getTopicNum() {
        return topicMap.size();
    }

    public static Boolean newTopic(String topicId, int partitionNum){
        synchronized (topicMap){
            if(isExist(topicId)){
                return false;
            }
            dbService.createTopic(topicId);
            return topicMap.put(topicId, topicFactory.create(topicId, partitionNum)) != null;
        }
    }

    public static Boolean removeTopic(String topicId) {
        synchronized (topicMap){
            if(!isExist(topicId)){
                return false;
            }
            dbService.removeTopic(topicId);
            return topicMap.remove(topicId) != null;
        }
    }

    public static Topic getTopicById(String topicId){
        return topicMap.get(topicId);
    }
}
