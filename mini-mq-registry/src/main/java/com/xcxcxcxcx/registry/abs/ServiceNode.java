package com.xcxcxcxcx.registry.abs;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface ServiceNode {

    void setHost(String host);

    void setPort(int port);

    /**
     * 全局唯一的ID
     * @return
     */
    String getId();

    /**
     * 服务名
     * @return
     */
    String getServiceName();

    /**
     * host
     * @return
     */
    String getHost();

    /**
     * 端口
     * @return
     */
    int getPort();

    default String getHostAndPort() {
        return getHost() + ":" + getPort();
    }

    default String getNodePath() {
        return getServiceName() + '/' + getId();
    }

}
