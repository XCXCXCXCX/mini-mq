package com.xcxcxcxcx.client.consumer;

import com.xcxcxcxcx.client.config.ClientConfig;
import com.xcxcxcxcx.mini.api.client.Consumer;

import java.util.Map;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class MiniConsumerFactory<T> implements ConsumerFactory<T>{

    private final ClientConfig clientConfig;

    private final Map<String, Object> props;

    public MiniConsumerFactory(ClientConfig clientConfig) {
        this(clientConfig, null);
    }

    public MiniConsumerFactory(ClientConfig clientConfig, Map<String, Object> props) {
        this.clientConfig = clientConfig;
        this.props = props;
    }

    @Override
    public Consumer<T> create(Class<T> tClass,
                              String id,
                              String topicId,
                              String key) {
        return new MiniConsumer<T>(tClass, clientConfig, id, topicId, key, props);
    }
}
