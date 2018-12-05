package com.xcxcxcxcx.mini.api.client;

import java.util.List;

/**
 *
 * 消费者组
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ConsumerGroup {

    private String groupId;

    private List<Consumer> consumers;

    /**
     * 组id
     * @return
     */
    public String getGroupId(){
        return groupId;
    }

    /**
     * 该组的消费者
     * @return
     */
    public List<Consumer> getConsumers(){
        return consumers;
    }
}
