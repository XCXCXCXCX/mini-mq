package com.xcxcxcxcx.client.spring.core;

import com.xcxcxcxcx.client.config.ClientConfig;
import com.xcxcxcxcx.client.consumer.MiniConsumer;

import java.util.Map;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class AutoMiniConsumer<T> extends MiniConsumer<T> {

    private MiniMessageListener<T> messageListener;

    private Boolean isBloking = Boolean.TRUE;

    public AutoMiniConsumer(Class<T> tClass, ClientConfig clientConfig, String consumerGroupId, String topicId, MiniMessageListener<T> messageListener) {
        super(tClass, clientConfig, consumerGroupId, topicId);
        this.messageListener = messageListener;
    }

    public AutoMiniConsumer(Class<T> tClass, ClientConfig clientConfig, String consumerGroupId, String topicId, String key, Map<String, Object> config, MiniMessageListener<T> messageListener) {
        super(tClass, clientConfig, consumerGroupId, topicId, key, config);
        this.messageListener = messageListener;
    }


    public void setMessageListener(MiniMessageListener<T> messageListener) {
        this.messageListener = messageListener;
    }

    public void init(){
        while(true){
            doConsume(getMessage(isBloking).getContent());
        }
    }

    public void doConsume(T t){
        messageListener.onSubscribe(t);
    }
}
