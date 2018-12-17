package com.xcxcxcxcx.registry.abs;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface DiscoveryClient {

    Boolean synStart();

    Boolean synStop();

    /**
     * 服务发现
     * @return
     */
    List<ServiceNode> getInstances(String serviceName);

    /**
     * 服务注册
     */
    void register(ServiceNode node);

    /**
     * 取消注册
     */
    void unregister(ServiceNode node);

}
