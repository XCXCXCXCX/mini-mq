package com.xcxcxcxcx.mini.common.message.entity;


import java.io.Serializable;
import java.util.List;


/**
 *
 * 消费消息
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class Pull implements Serializable {

    /**
     * 消费指定topicId
     */
    public String topicId;

    /**
     * 路由key，用于固定消费指定分区
     */
    public String key;

}
