package com.xcxcxcxcx.demo.condition3.consumer;

import com.xcxcxcxcx.demo.pub.config.TestConfig;
import com.xcxcxcxcx.demo.pub.consumer.ConsumerSlowly;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class G1_1_T1 extends ConsumerSlowly {

    /**
     * 订阅topic为t1，消费组ID为g1
     * 每2秒消费1条消息
     */
    public G1_1_T1() {
        super(2, 1, TestConfig.group_g1, TestConfig.topic_t1);
    }

    public static void main(String[] args) {
        new G1_1_T1().startConsume();
    }
}
