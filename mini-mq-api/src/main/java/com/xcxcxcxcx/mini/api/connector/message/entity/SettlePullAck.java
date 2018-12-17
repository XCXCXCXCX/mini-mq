package com.xcxcxcxcx.mini.api.connector.message.entity;


import com.xcxcxcxcx.mini.api.connector.message.entity.Settle;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class SettlePullAck extends Settle {

    public String key;

    /**
     *
     * 客户端已发送pullReject请求但pullReject的消息在超时时间内未收到响应
     *
     */
    public List<Long> rejectIds;
}
