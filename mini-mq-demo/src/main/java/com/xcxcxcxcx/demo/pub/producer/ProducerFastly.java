package com.xcxcxcxcx.demo.pub.producer;

/**
 * 1s生产10000条消息
 * @author XCXCXCXCX
 * @since 1.0
 */
public class ProducerFastly extends AbstractProducer{

    public ProducerFastly(String id, String topicId) {
        super(1, 10000, id, topicId);
    }
}
