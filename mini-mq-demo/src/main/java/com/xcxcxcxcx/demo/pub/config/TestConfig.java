package com.xcxcxcxcx.demo.pub.config;

import com.xcxcxcxcx.client.config.ClientConfig;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface TestConfig {

    ClientConfig clientConfig = new ClientConfig.ClientConfigBuilder(4402).setHost("localhost")
            .setRegistryConnectAddress("192.168.179.130:2181").build();

    String topic_t1 = "topic1";

    String topic_t2 = "topic2";

    String producer_p1 = "producer1";

    String producer_p2 = "producer2";

    String group_g1 = "group1";

    String group_g2 = "group2";

}
