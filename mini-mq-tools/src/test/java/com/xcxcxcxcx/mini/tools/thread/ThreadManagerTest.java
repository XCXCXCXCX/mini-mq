package com.xcxcxcxcx.mini.tools.thread;

import org.junit.Test;

import java.io.IOException;

/**
 *
 * @author XCXCXCXCX
 * @since 1.0
 */
public class ThreadManagerTest {

    @Test
    public void createAndGetThreadInfo() throws IOException, InterruptedException {

        for(int i = 0; i < 10; i++){
            ThreadManager.newThread(()->{
                System.out.println("i'm " + Thread.currentThread().getName());
                while(!Thread.currentThread().isInterrupted()){

                }
            },"xc").start();
        }
        for(int i = 0; i < 10; i++){
            ThreadManager.newThread(()->{
                System.out.println("i'm " + Thread.currentThread().getName());
                while(!Thread.currentThread().isInterrupted()){

                }
                System.out.println("end : " + Thread.currentThread().getName());
            },"haha").start();
        }


        System.in.read();

    }


}
