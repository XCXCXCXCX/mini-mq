package com.xcxcxcxcx.registry.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class CuratorManager {

    private static CuratorFramework client;

    private static Boolean isInit(){
        return client != null;
    }

    public static synchronized CuratorFramework getClient(String connectString){
        if(!isInit()){
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            client = CuratorFrameworkFactory.builder()
                            .connectString(connectString)
                            .sessionTimeoutMs(5000)
                            .connectionTimeoutMs(5000)
                            .retryPolicy(retryPolicy)
                            .build();
        }
        return client;
    }
}