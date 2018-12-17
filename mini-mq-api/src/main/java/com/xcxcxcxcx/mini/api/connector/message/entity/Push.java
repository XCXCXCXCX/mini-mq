package com.xcxcxcxcx.mini.api.connector.message.entity;

import com.xcxcxcxcx.mini.api.connector.message.MessageContentHolder;

import java.io.Serializable;


/**
 *
 * 发送消息
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class Push extends MessageContentHolder implements Serializable {
    /**
     * 请求ID
     */
    public int id;
}
