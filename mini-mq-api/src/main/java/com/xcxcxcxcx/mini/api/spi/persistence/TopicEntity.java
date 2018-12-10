package com.xcxcxcxcx.mini.api.spi.persistence;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class TopicEntity{
    private Integer id;
    private String topicId;
    private Integer partitionNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    @Override
    public String toString() {
        return "TopicEntity{" +
                "topicId='" + topicId + '\'' +
                ", partitionNum=" + partitionNum +
                '}';
    }
}