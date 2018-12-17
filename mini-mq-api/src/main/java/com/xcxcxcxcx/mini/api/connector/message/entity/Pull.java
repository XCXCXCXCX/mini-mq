package com.xcxcxcxcx.mini.api.connector.message.entity;


import java.io.Serializable;


/**
 *
 * 消费消息
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class Pull implements Serializable {

    /**
     * 路由key，用于固定消费指定分区
     */
    public String key;

    /**
     * pull消息数量
     */
    public int num;

}
