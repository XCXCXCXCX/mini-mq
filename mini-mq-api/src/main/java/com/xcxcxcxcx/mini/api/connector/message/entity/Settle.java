package com.xcxcxcxcx.mini.api.connector.message.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class Settle implements Serializable{

    /**
     * 客户端已发送pushAck请求但pushAck的消息在超时时间内未收到响应
     *
     * 客户端已发送pullAck请求但pullAck的消息在超时时间内未收到响应
     *
     */
    public List<Long> ackIds;
}
