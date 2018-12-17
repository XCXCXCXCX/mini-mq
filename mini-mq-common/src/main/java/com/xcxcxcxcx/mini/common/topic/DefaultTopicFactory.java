package com.xcxcxcxcx.mini.common.topic;


import java.util.concurrent.ScheduledExecutorService;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class DefaultTopicFactory implements Factory<DefaultTopic>{

    @Override
    public DefaultTopic create() {
        return create("default-topic",null, 1);
    }

    public DefaultTopic create(String topicId,
                               String groupId,
                               int partitionNum) {
        return new DefaultTopic(topicId,
                groupId,
                partitionNum);
    }

}
