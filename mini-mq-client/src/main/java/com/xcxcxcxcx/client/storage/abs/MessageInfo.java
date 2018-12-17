package com.xcxcxcxcx.client.storage.abs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class MessageInfo {

    private Long id;

    private Long expired;

    private Integer status;


    public Long getId() {
        return id;
    }

    public MessageInfo setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getExpired() {
        return expired;
    }

    public MessageInfo setExpired(Long expired) {
        this.expired = expired;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public MessageInfo setStatus(Integer status) {
        this.status = status;
        return this;
    }
}
