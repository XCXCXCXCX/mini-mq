package com.xcxcxcxcx.client.producer;

import com.xcxcxcxcx.mini.api.client.Producer;


/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface ProducerFactory<T> {


    Producer<T> create();

}
