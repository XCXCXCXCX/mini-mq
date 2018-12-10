package com.xcxcxcxcx.persistence.db.persistence;

import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.persistence.PersistenceMapper;
import com.xcxcxcxcx.mini.api.persistence.PersistenceType;
import com.xcxcxcxcx.mini.api.spi.persistence.PersistenceService;
import com.xcxcxcxcx.mini.api.spi.persistence.PersistenceServiceFactory;
import com.xcxcxcxcx.mini.api.spi.persistence.TopicEntity;

import java.util.List;

/**
 * 代理对象，可以对PersistenceService增强
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class DbMapper implements PersistenceMapper<Message>{

    private final PersistenceService<Message> service = PersistenceServiceFactory.create();

    @Override
    public PersistenceType getPersistenceType() {
        return PersistenceType.DB;
    }

    @Override
    public PersistenceService<Message> getPersistenceService() {
        return service;
    }

    @Override
    public Message push(Message message) {
        return service.push(message);
    }

    @Override
    public List<Message> batchPush(List<Message> messages) {
        return service.batchPush(messages);
    }

    @Override
    public Long ackPush(Long id) {
        return service.ackPush(id);
    }

    @Override
    public List<Long> batchAckPush(List<Long> ids) {
        return service.batchAckPush(ids);
    }

    @Override
    public Long rejectPush(Long id) {
        return service.rejectPush(id);
    }

    @Override
    public List<Long> batchRejectPush(List<Long> ids) {
        return service.batchRejectPush(ids);
    }

    @Override
    public List<Message> prePullIfAbsent(String topicId, String consumerGroupId, Integer pageNum, Integer pageSize) {
        return service.prePullIfAbsent(topicId, consumerGroupId, pageNum, pageSize);
    }

    @Override
    public List<Message> prePull(String topicId, String consumerGroupId, Integer pageNum, Integer pageSize) {
        return service.prePull(topicId, consumerGroupId, pageNum, pageSize);
    }

    @Override
    public Long ackPull(Long id, String consumerGroupId) {
        return service.ackPull(id, consumerGroupId);
    }

    @Override
    public List<Long> batchAckPull(List<Long> ids, String consumerGroupId) {
        return service.batchAckPull(ids, consumerGroupId);
    }

    @Override
    public Long rejectPull(Long id, String consumerGroupId) {
        return service.rejectPull(id, consumerGroupId);
    }

    @Override
    public List<Long> batchRejectPull(List<Long> ids, String consumerGroupId) {
        return service.batchRejectPull(ids, consumerGroupId);
    }

    @Override
    public Boolean cleanExpired() {
        return service.cleanExpired();
    }

    @Override
    public Boolean createTopic(String topicId, Integer partitionNum) {
        return service.createTopic(topicId, partitionNum);
    }

    @Override
    public Boolean removeTopic(String topicId) {
        return service.removeTopic(topicId);
    }

    @Override
    public List<TopicEntity> loadAllTopic() {
        return service.loadAllTopic();
    }
}
