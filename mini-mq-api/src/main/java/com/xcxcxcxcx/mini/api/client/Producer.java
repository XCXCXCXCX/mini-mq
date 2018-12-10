package com.xcxcxcxcx.mini.api.client;


/**
 *
 * 生产者
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface Producer extends Partner{

    /**
     * 生产者发布的topicId
     * @return
     */
    String getTopicId();

}
