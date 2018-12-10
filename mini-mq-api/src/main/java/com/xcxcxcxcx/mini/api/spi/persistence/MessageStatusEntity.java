package com.xcxcxcxcx.mini.api.spi.persistence;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class MessageStatusEntity {

    private Long id;

    private Long mid;

    private String consumerGroupId;

    private Integer pulledTimes;

    private Long expired;

    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getConsumerGroupId() {
        return consumerGroupId;
    }

    public void setConsumerGroupId(String consumerGroupId) {
        this.consumerGroupId = consumerGroupId;
    }

    public Integer getPulledTimes() {
        return pulledTimes;
    }

    public void setPulledTimes(Integer pulledTimes) {
        this.pulledTimes = pulledTimes;
    }

    public Long getExpired() {
        return expired;
    }

    public void setExpired(Long expired) {
        this.expired = expired;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
