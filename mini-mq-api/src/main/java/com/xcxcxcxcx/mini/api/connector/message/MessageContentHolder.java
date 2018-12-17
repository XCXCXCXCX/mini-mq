package com.xcxcxcxcx.mini.api.connector.message;

import java.io.Serializable;
import java.util.List;

/**
 *
 * 消息持有者
 * @author XCXCXCXCX
 * @since 1.0
 */
public abstract class MessageContentHolder implements Serializable{

    /**
     * 多条消息内容
     */
    public List<Message> messages;

}
