package com.xcxcxcxcx.client.spring.core;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface MiniMessageListener<T> {

    void onSubscribe(T t);

}
