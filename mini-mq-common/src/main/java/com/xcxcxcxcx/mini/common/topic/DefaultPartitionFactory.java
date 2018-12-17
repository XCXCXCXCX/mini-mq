package com.xcxcxcxcx.mini.common.topic;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class DefaultPartitionFactory implements Factory<BasePartition>{

    private final BaseTopic topic;

    public DefaultPartitionFactory(BaseTopic topic) {
        this.topic = topic;
    }

    @Override
    public BasePartition create() {
        return new DefaultPartition(topic.getId(), topic.getAndIncr());
    }

    public List<BasePartition> createList(int size) {
        List<BasePartition> partitions = new CopyOnWriteArrayList<>();
        for(int i = 0; i < size; i++){
            partitions.add(create());
        }
        return partitions;
    }
}
