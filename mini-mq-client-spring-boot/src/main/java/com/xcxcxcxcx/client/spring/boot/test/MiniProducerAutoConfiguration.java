package com.xcxcxcxcx.client.spring.boot.test;

import com.xcxcxcxcx.client.config.ClientConfig;
import com.xcxcxcxcx.client.config.RoleConfig;
import com.xcxcxcxcx.client.producer.MiniProducerFactory;
import com.xcxcxcxcx.client.spring.core.MiniTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
@Configuration
public class MiniProducerAutoConfiguration {

    @Bean
    public ClientConfig clientConfig(){
        return new ClientConfig.ClientConfigBuilder(4402)
                .setHost("localhost")
                .setRegistryConnectAddress("192.168.179.130:2181")
                .build();
    }

    @Bean
    public MiniProducerFactory miniProducerFactory(){
        Map<String, Object> props = new HashMap();
        props.put(RoleConfig.one_prefetch_max_num, 3000);
        return new MiniProducerFactory<String>(clientConfig(),
                "firstProducer", "firstTopic",
                null, props);
    }

    @Bean
    public MiniTemplate miniTemplate(){
        return new MiniTemplate(miniProducerFactory());
    }
}
