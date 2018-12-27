package com.xcxcxcxcx.client.consumer;


import com.xcxcxcxcx.mini.api.client.Consumer;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface ConsumerFactory<T> {

    Consumer<T> create(Class<T> tClass,
                       String id,
                       String topicId,
                       String key);
}
