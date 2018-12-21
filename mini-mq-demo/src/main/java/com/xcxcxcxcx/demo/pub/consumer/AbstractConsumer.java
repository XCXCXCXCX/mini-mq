package com.xcxcxcxcx.demo.pub.consumer;

import com.xcxcxcxcx.client.consumer.MiniConsumer;
import com.xcxcxcxcx.client.producer.MiniProducer;
import com.xcxcxcxcx.demo.pub.config.TestConfig;
import com.xcxcxcxcx.demo.pub.entity.InfoEntity;
import com.xcxcxcxcx.mini.api.client.BaseMessage;

import java.util.concurrent.CompletableFuture;


/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public abstract class AbstractConsumer extends MiniConsumer<InfoEntity> {

    private final int seconds;

    private final int num;

    private final long percent;

    private int idGenerator = 0;

    private final long start = System.currentTimeMillis();

    public AbstractConsumer(int seconds, int num, String id, String topicId){
        super(TestConfig.clientConfig,id,topicId);
        this.seconds = seconds;
        this.num = num;
        this.percent = (seconds * 1000)/num;
    }

    /**
     * 无限循环消费
     */
    public void startConsume(){
        while(true){
            consume();
        }
    }

    /**
     * 有限时间循环消费
     * @param millisecond
     */
    public void startConsume(long millisecond){
        while(System.currentTimeMillis() - start <= millisecond){
            consume();
        }
    }

    protected void consume(){
        doWait();
        doConsume();
    }

    protected void doConsume(){
        BaseMessage<InfoEntity> entity = getMessage(InfoEntity.class,true);
        System.out.println(++idGenerator + "获得消息" + entity.getContent().toString());
        CompletableFuture<Boolean> completableFuture = ack(entity.getMid());
        completableFuture.whenComplete(((aBoolean, throwable) -> {
            if(aBoolean){
                System.out.println("消费{mid="+ entity.getMid() +"}消息" + entity.toString() + "--->成功");
            }else{
                System.out.println("消费{mid="+ entity.getMid() +"}消息" + entity.toString() + "--->失败");
            }
        }));
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
