package com.xcxcxcxcx.demo.condition4.producer;

import com.xcxcxcxcx.demo.pub.config.TestConfig;
import com.xcxcxcxcx.demo.pub.producer.ProducerSlowly;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class P1_T2 extends ProducerSlowly{


    /**
     * 订阅topic为t1，生产者ID为p2
     */
    public P1_T2() {
        super(2, 1, TestConfig.producer_p1, TestConfig.topic_t2);
    }

    public static void main(String[] args) {
        new P1_T2().startProduce();
    }
}
