package com.xcxcxcxcx.mini.api.connector.message.entity;



/**
 *
 * 消息消费的确认
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PullAck extends Ack {
    /**
     * 作为拒绝用的key，将拒绝成功的消息根据key拉取到指定partition
     */
    public String key;
}
