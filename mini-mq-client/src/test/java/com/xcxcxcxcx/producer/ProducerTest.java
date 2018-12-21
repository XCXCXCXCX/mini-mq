package com.xcxcxcxcx.producer;

import com.xcxcxcxcx.client.config.ClientConfig;
import com.xcxcxcxcx.client.producer.MiniProducer;
import com.xcxcxcxcx.mini.api.client.Producer;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class ProducerTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setHostAndPort(null);
        clientConfig.setRegistryConnectAddress("192.168.179.130:2181");
        clientConfig.setServiceName(ClientConfig.DEFAULT_SERVICE_NAME);
        Producer producer = new MiniProducer<String>(clientConfig,
                "test-producer-1",
                "test-topic-1");
        for(int i = 0;i < 10; i++){
            Thread.sleep(1000);
            CompletableFuture<Boolean> callback =
                    producer.send("hello i'm " + i + " msg");
            final int j = i;
            callback.whenComplete(new BiConsumer<Boolean, Throwable>() {
                @Override
                public void accept(Boolean aBoolean, Throwable throwable) {
                    if(throwable != null){
                        throwable.printStackTrace();
                    }
                    if(aBoolean){
                        System.out.println("发送成功 : " + j);
                    }else{
                        System.out.println("发送失败 ： " + j);
                    }
                }
            });
        }
        System.in.read();
    }
}
