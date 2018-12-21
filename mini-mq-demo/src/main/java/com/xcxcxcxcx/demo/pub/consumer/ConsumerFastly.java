package com.xcxcxcxcx.demo.pub.consumer;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class ConsumerFastly extends AbstractConsumer{

    public ConsumerFastly(String id, String topicId) {
        super(1, 10000, id, topicId);
    }

}
