package com.xcxcxcxcx.client.spring.boot.test;

import com.xcxcxcxcx.client.config.ClientConfig;
import com.xcxcxcxcx.client.config.RoleConfig;
import com.xcxcxcxcx.client.spring.core.AutoMiniConsumer;
import com.xcxcxcxcx.client.spring.test.MyMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
@Configuration
public class MiniConsumerAutoConfiguration {

    @Bean
    public ClientConfig clientConfig2(){
        return new ClientConfig.ClientConfigBuilder(4402)
                .setHost("localhost")
                .setRegistryConnectAddress("192.168.179.130:2181")
                .build();
    }

    @Bean
    public MyMessageListener messageListener(){
        return new MyMessageListener();
    }

    @Bean
    public AutoMiniConsumer miniConsumer(){
        Map<String, Object> props = new HashMap();
        props.put(RoleConfig.one_prefetch_max_num, 3000);
        return new AutoMiniConsumer(String.class, clientConfig2(),
                "firstConsumerGroup", "firstTopic",
                null, props, messageListener());
    }

}
