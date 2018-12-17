package com.xcxcxcxcx.mini.common.topic;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class DefaultTopic extends BaseTopic{

    public DefaultTopic(String topicId,
                        String groupId,
                        int partitionNum) {
        super(topicId, groupId, partitionNum);
    }

    public DefaultTopic(String topicId,
                        String groupId) {
        super(topicId, groupId);
    }


}
