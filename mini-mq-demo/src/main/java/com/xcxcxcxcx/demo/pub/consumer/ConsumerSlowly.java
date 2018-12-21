package com.xcxcxcxcx.demo.pub.consumer;


/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class ConsumerSlowly extends AbstractConsumer{

    public ConsumerSlowly(String id, String topicId) {
        super(1, 1, id, topicId);
    }

    public ConsumerSlowly(int seconds, int num, String id, String topicId) {
        super(seconds, num, id, topicId);
    }
}
