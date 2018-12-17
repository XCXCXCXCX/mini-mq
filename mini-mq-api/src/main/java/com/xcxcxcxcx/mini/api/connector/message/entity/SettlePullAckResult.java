package com.xcxcxcxcx.mini.api.connector.message.entity;


import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class SettlePullAckResult extends Settle {

    /**
     * 传递回来的消息id，表示这些id均被broker确认成功，即：client和server达成共识
     */
    public List<Long> rejectIds;
}
