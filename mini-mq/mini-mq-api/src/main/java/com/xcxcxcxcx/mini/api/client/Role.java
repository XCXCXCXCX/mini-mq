package com.xcxcxcxcx.mini.api.client;

import java.util.Properties;

/**
 *
 * 生产者/消费者角色，及其配置
 * @author XCXCXCXCX
 * @Since 1.0
 */
public final class Role {

    public static final String PRODUCER = "producer";

    public static final String CONSUMER ="consumer";

    public static final String PRODUCER_AND_CONSUMER = "producer&consumer";

    private String roleName;

    private Properties producerConfig;

    private Properties consumerConfig;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Properties getProducerConfig() {
        return producerConfig;
    }

    public void setProducerConfig(Properties producerConfig) {
        this.producerConfig = producerConfig;
    }

    public Properties getConsumerConfig() {
        return consumerConfig;
    }

    public void setConsumerConfig(Properties consumerConfig) {
        this.consumerConfig = consumerConfig;
    }
}
