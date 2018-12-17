package com.xcxcxcxcx.mini.common.topic.entity;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ProducerEntity {

    private String id;

    private String topicId;

    public static ProducerEntity build(){
        return new ProducerEntity();
    }

    public ProducerEntity setId(String id){
        this.id = id;
        return this;
    }

    public ProducerEntity setTopicId(String topicId){
        this.topicId = topicId;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getTopicId() {
        return topicId;
    }
}
