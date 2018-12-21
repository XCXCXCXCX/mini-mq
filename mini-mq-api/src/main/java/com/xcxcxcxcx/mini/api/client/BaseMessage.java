package com.xcxcxcxcx.mini.api.client;

/**
 *
 * 用于客户端对消息的处理，如消费、确认
 * @author XCXCXCXCX
 * @since 1.0
 */
public class BaseMessage<T>{

    private Long mid;

    private T content;

    public Long getMid() {
        return mid;
    }

    public BaseMessage<T> setMid(Long mid) {
        this.mid = mid;
        return this;
    }

    public T getContent() {
        return content;
    }

    public BaseMessage<T> setContent(T content) {
        this.content = content;
        return this;
    }
}
