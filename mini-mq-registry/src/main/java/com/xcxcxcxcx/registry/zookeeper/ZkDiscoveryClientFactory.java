package com.xcxcxcxcx.registry.zookeeper;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ZkDiscoveryClientFactory{


    public static ZkDiscoveryClient create(String connectString){
        return new ZkDiscoveryClient(connectString);
    }

}
