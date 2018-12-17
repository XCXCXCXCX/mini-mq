package com.xcxcxcxcx.mini.common.topic;

import com.xcxcxcxcx.mini.api.client.Consumer;
import com.xcxcxcxcx.mini.api.connector.topic.Topic;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class DefaultPartition extends BasePartition{

    public DefaultPartition(String topicId, int id) {
        super(topicId, id);
    }

    @Override
    public void subscribe(Consumer consumer) {

    }

    @Override
    public void unsubscribe(Consumer consumer) {

    }

    @Override
    public int getSubscribeNum() {
        return 0;
    }

    @Override
    public int getPublishNum() {
        return 0;
    }

}
