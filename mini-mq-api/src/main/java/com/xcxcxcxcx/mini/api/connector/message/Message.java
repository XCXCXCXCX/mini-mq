package com.xcxcxcxcx.mini.api.connector.message;


/**
 *
 * 消息实体，对应数据库表的row
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class Message {

    /**
     * 全局唯一的消息ID
     */
    private Long mid;

    /**
     * 消息所属topicId
     */
    private String topicId;

    /**
     * 消息状态
     * 0:新建（有超时时间，超时清除）
     * 1:新建且已确认（无超时时间）
     * 2:正在处理（有超时时间，超时转化成1）
     * 3:消费失败，待处理（无超时时间，通过consumer定期pull，过滤掉已消费成功的消息，并修改状态）
     * 4:消费成功，已处理（无超时时间）
     */
    private Integer status;

    /**
     * 被pull的次数
     * 从新建态->正在处理态时会增加pull次数
     * 超过指定次数，将不会再被pull
     */
    //private Integer pulledTimes;

    /**
     * 超时时间
     * 当status=0时，超时会导致消息被delete
     * 当status=1时，超时时间无效
     * 当status=2时，超时会导致消息status变为1
     * 当status=3时，超时时间无效
     * 当status=4时，超时时间无效
     */
    private Long expired;

    /**
     * 消息内容
     */
    private String content;

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getExpired() {
        return expired;
    }

    public void setExpired(Long expired) {
        this.expired = expired;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "mid=" + mid +
                ", topicId='" + topicId + '\'' +
                ", status=" + status +
                ", expired=" + expired +
                ", content='" + content + '\'' +
                '}';
    }

    public enum MessageStatus{
        NEW(0),
        NEW_ACK(1),
        PROCCESSIGN(2),
        CONSUME_FAILED(3),
        CONSUMER_SUCCESS(4);

        private int id;

        MessageStatus(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

}
