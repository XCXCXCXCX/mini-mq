package com.xcxcxcxcx.mini.api.spi.persistence;

import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.spi.Spi;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @Since 1.0
 */
@Spi(order = 1000)
public class PersistenceServiceImplTest implements PersistenceService<Message> {

    @Override
    public Message save(Message message) {
        return null;
    }

    @Override
    public List<Message> saveList(List<Message> messages) {
        return null;
    }

    @Override
    public List<Message> saveList(List<Long> ids, String oldTopicId, Integer oldStatus, Integer fromPulledTimes, Integer toPulledTimes, Long oldExpired, Integer newStatus, Integer plusTimes, Long newExpired) {
        return null;
    }

    @Override
    public Message saveIfAbsent(Message message) {
        return null;
    }

    @Override
    public List<Message> saveListIfAbsent(List<Message> messages) {
        return null;
    }

    @Override
    public Boolean remove(Long id) {
        return null;
    }

    @Override
    public Boolean removeList(List<Long> idList, String topicId, Integer status, Integer fromPulledTimes, Integer toPulledTimes, Long expired) {
        return null;
    }

    @Override
    public List<Message> query(List<Long> idList, String topicId, Integer status, Integer fromPulledTimes, Integer toPulledTimes, Long expired, String blurContent, Integer pageNum, Integer pageSize) {
        return null;
    }

    @Override
    public Boolean createTopic(String topicId, Integer partitionNum) {
        return null;
    }

    @Override
    public Boolean removeTopic(String topicId) {
        return null;
    }

    @Override
    public List<TopicEntity> loadAllTopic() {
        return null;
    }
}
