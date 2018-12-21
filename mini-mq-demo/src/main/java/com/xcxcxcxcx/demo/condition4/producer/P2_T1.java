package com.xcxcxcxcx.demo.condition4.producer;

import com.xcxcxcxcx.demo.pub.config.TestConfig;
import com.xcxcxcxcx.demo.pub.producer.ProducerSlowly;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class P2_T1 extends ProducerSlowly{


    /**
     * 订阅topic为t1，生产者ID为p2
     * 每2秒生产一条
     */
    public P2_T1() {
        super(2, 1, TestConfig.producer_p2, TestConfig.topic_t1);
    }

    public static void main(String[] args) {
        new P2_T1().startProduce();
    }
}
