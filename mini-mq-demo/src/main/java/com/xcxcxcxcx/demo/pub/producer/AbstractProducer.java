package com.xcxcxcxcx.demo.pub.producer;


import com.xcxcxcxcx.client.producer.MiniProducer;
import com.xcxcxcxcx.demo.pub.config.TestConfig;
import com.xcxcxcxcx.demo.pub.entity.InfoEntity;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public abstract class AbstractProducer extends MiniProducer<InfoEntity> {

    private final int seconds;

    private final int num;

    private final long percent;

    private final long start = System.currentTimeMillis();

    private int idGenerator = 0;

    private int successId = 0;

    public AbstractProducer(int seconds, int num, String id, String topicId){
        super(TestConfig.clientConfig,id,topicId);
        this.seconds = seconds;
        this.num = num;
        this.percent = (seconds * 1000)/num;
    }

    /**
     * 无限循环消费
     */
    public void startProduce(){
        while(true){
            produce();
        }
    }

    /**
     * 有限时间循环消费
     * @param millisecond
     */
    public void startProduce(long millisecond){
        while(System.currentTimeMillis() - start <= millisecond){
            produce();
        }
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送count条消息
     * @param count
     */
    public void startProduce(int count){
        while(idGenerator < count){
            produce();
        }
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void produce(){
        doWait();
        InfoEntity entity = doProduce();
        CompletableFuture<Boolean> completableFuture = send(entity);
        completableFuture.whenComplete(((aBoolean, throwable) -> {
            if(aBoolean){
                System.out.println(++successId +"发送消息" + entity.getId() + "--->成功");
            }else{
                System.out.println("发送消息" + entity.getId() + "--->失败");
            }
        }));
    }

    public InfoEntity doProduce(){
        InfoEntity info = new InfoEntity();
        info.setId(++idGenerator);
        info.setContent("i'm "+ idGenerator +" message");
        info.setCreateOn(new Date());
        return info;
    }

    /**
     * seconds秒内生产num条消息
     */
    public void doWait(){
        try {
            Thread.sleep(percent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
