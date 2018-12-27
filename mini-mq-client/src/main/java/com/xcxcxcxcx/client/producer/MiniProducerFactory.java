package com.xcxcxcxcx.client.producer;

import com.xcxcxcxcx.client.config.ClientConfig;
import com.xcxcxcxcx.mini.api.client.Producer;

import java.util.Map;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class MiniProducerFactory<T> implements ProducerFactory<T>{

    private final ClientConfig clientConfig;

    private final String id;

    private final String topicId;

    private final String key;

    private final Map<String, Object> props;

    public MiniProducerFactory(ClientConfig clientConfig, String id, String topicId) {
        this(clientConfig, id, topicId, null, null);
    }

    public MiniProducerFactory(ClientConfig clientConfig, String id, String topicId, String key) {
        this(clientConfig, id, topicId, key, null);
    }

    public MiniProducerFactory(ClientConfig clientConfig, String id, String topicId, Map<String, Object> props) {
        this(clientConfig, id, topicId, null, props);
    }

    public MiniProducerFactory(ClientConfig clientConfig, String id, String topicId, String key, Map<String, Object> props) {
        this.clientConfig = clientConfig;
        this.id = id;
        this.topicId = topicId;
        this.key = key;
        this.props = props;
    }

    @Override
    public Producer<T> create() {
        return new MiniProducer<T>(clientConfig, id, topicId, key, props);
    }
}
