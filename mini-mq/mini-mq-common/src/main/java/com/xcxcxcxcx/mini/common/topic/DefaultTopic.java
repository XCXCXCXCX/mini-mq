package com.xcxcxcxcx.mini.common.topic;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class DefaultTopic extends BaseTopic{

    public DefaultTopic(String topicId, int partitionNum) {
        super(topicId, partitionNum);
    }

    public DefaultTopic(String topicId) {
        super(topicId);
    }


}
