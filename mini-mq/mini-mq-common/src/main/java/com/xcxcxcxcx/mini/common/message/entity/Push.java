package com.xcxcxcxcx.mini.common.message.entity;

import com.xcxcxcxcx.mini.api.connector.message.MessageHolder;

import java.io.Serializable;


/**
 *
 * 发送消息
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class Push extends MessageHolder implements Serializable {

    public String topicId;

    /**
     * 用于路由的key
     */
    public String key;

}
