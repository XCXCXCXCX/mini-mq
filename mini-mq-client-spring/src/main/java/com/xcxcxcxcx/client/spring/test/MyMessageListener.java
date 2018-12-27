package com.xcxcxcxcx.client.spring.test;

import com.xcxcxcxcx.client.spring.core.MiniMessageListener;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class MyMessageListener implements MiniMessageListener<String>{

    @Override
    public void onSubscribe(String o) {
        System.out.println("收到一条消息.." + o);
    }
}
