package com.xcxcxcxcx.mini.common.topic.entity;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ConsumerEntity {

    private String id;

    private int idInGroup;

    private String topicId;

    public static ConsumerEntity build(){
        return new ConsumerEntity();
    }

    public ConsumerEntity setId(String id){
        this.id = id;
        return this;
    }

    public ConsumerEntity setIdInGroup(int idInGroup){
        this.idInGroup = idInGroup;
        return this;
    }

    public ConsumerEntity setTopicId(String topicId){
        this.topicId = topicId;
        return this;
    }

    public String getId() {
        return id;
    }

    public int getIdInGroup() {
        return idInGroup;
    }

    public String getTopicId() {
        return topicId;
    }

}
