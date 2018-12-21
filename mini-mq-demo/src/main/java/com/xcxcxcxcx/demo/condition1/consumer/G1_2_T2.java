package com.xcxcxcxcx.demo.condition1.consumer;

import com.xcxcxcxcx.demo.pub.config.TestConfig;
import com.xcxcxcxcx.demo.pub.consumer.ConsumerSlowly;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class G1_2_T2 extends ConsumerSlowly{

    /**
     * 订阅topic为t2，消费组ID为g1
     */
    public G1_2_T2() {
        super(TestConfig.group_g1, TestConfig.topic_t2);
    }

    public static void main(String[] args) {
        new G1_2_T2().startConsume();
    }
}
