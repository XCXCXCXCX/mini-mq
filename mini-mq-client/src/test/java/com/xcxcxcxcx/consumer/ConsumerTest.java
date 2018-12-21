package com.xcxcxcxcx.consumer;

import com.xcxcxcxcx.client.config.ClientConfig;
import com.xcxcxcxcx.client.consumer.MiniConsumer;
import com.xcxcxcxcx.mini.api.client.BaseMessage;
import com.xcxcxcxcx.mini.api.client.Consumer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class ConsumerTest {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setHostAndPort(null);
        clientConfig.setRegistryConnectAddress("192.168.179.130:2181");
        clientConfig.setServiceName(ClientConfig.DEFAULT_SERVICE_NAME);
        Consumer<String> consumer = new MiniConsumer<String>(clientConfig,
                "test-group-1",
                "test-topic-1");

        for(int i = 0;; i++){
            Thread.sleep(1000);
            BaseMessage<String> message = consumer.getMessage(String.class, true);
            System.out.println( i + " ==> consume message : " + message.getContent());
            if(i > 100){
                System.out.println("超过100了!");
                System.out.println("consume message : " + message.getContent());
            }
            CompletableFuture<Boolean> future = consumer.reject(message.getMid());
            final int j = i;
            future.whenComplete((flag,throwable)->{
                if(throwable != null){
                    throwable.printStackTrace();
                }
                if(flag){
                    System.out.println("consume reject message {id=" + j + "}: " + flag);
                }else{
                    System.out.println("consume reject message {id=" + j + "}: " + flag);
                }
            });
        }
    }
}
