package com.xcxcxcxcx.mini.common.topic;


/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class DefaultTopicFactory implements Factory<DefaultTopic>{

    @Override
    public DefaultTopic create() {
        return create("default-topic", 1);
    }

    public DefaultTopic create(String topicId, int partitionNum) {
        return new DefaultTopic(topicId, partitionNum);
    }

}
