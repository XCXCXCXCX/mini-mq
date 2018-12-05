package com.xcxcxcxcx.mini.api.client;

import java.util.Properties;

/**
 *
 * 消费者有唯一的id
 * 由groupId-id拼接而成
 *
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface Consumer extends Partner{

    /**
     * 消费者所属group
     */
    ConsumerGroup getGroup();

    /**
     * 组内id
     * @return
     */
    int getIdInGroup();

    /**
     * 消费者订阅的topicId
     * @return
     */
    String getTopicId();


}
