package com.xcxcxcxcx.mini.common.topic.task;

import com.xcxcxcxcx.mini.api.connector.message.AutoPullToMemoryTask;
import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.persistence.PersistenceMapper;
import com.xcxcxcxcx.mini.common.topic.BrokerContext;
import com.xcxcxcxcx.persistence.db.persistence.DbFactory;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class RetryConsumptionTask implements AutoPullToMemoryTask{

    private PersistenceMapper persistenceMapper = DbFactory.getMapper();

    private final List<Long> messageRejectIds;

    private final String topicId;

    private final String groupId;

    private final String key;

    private final int num;

    public RetryConsumptionTask(List<Long> messageRejectIds,
                                String topicId,
                                String groupId,
                                String key,
                                int num) {
        this.messageRejectIds = messageRejectIds;
        this.topicId = topicId;
        this.groupId = groupId;
        this.key = key;
        this.num = num;
    }

    @Override
    public Boolean autoPullCondition() {
        return BrokerContext.isExist(topicId) && BrokerContext.isExistGroup(groupId);
    }

    @Override
    public void doPullToMemory() {
        if(autoPullCondition()){
            List<Message> messages =
                    persistenceMapper.
                            prePull(messageRejectIds, topicId, groupId, key,1, num);

            if(key == null){
                BrokerContext.sendMessage(topicId, messages);
            }else{
                BrokerContext.sendMessage(key, topicId, messages);
            }
        }
    }

    @Override
    public void run() {
        doPullToMemory();
    }
}
