package com.xcxcxcxcx.demo.pub.producer;


/**
 *
 * 1s生产1条消息
 * @author XCXCXCXCX
 * @since 1.0
 */
public class ProducerSlowly extends AbstractProducer{


    public ProducerSlowly(String id, String topicId) {
        super(1, 1, id, topicId);
    }

    public ProducerSlowly(int seconds, int num, String id, String topicId) {
        super(seconds, num, id, topicId);
    }
}
