package com.xcxcxcxcx.mini.api.connector.message.entity;


import java.io.Serializable;
import java.util.List;

/**
 *
 * 确认消息
 * @author XCXCXCXCX
 * @since 1.0
 */
public class Ack implements Serializable{

    /**
     * 请求ID
     */
    public int id;

    /**
     * 确认的消息ID
     */
    public List<Long> messageAckIds;

    /**
     * 拒绝的消息ID
     */
    public List<Long> messageRejectIds;

}
