package com.xcxcxcxcx.client.config;

import java.net.InetSocketAddress;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ClientConfig {

    private static final String DEFAULT_SERVICE_NAME = "broker";

    private InetSocketAddress hostAndPort;

    private String serviceName = DEFAULT_SERVICE_NAME;

    private String registryConnectAddress;

    public InetSocketAddress getHostAndPort() {
        return hostAndPort;
    }

    public void setHostAndPort(InetSocketAddress hostAndPort) {
        this.hostAndPort = hostAndPort;
    }

    public String getRegistryConnectAddress() {
        return registryConnectAddress;
    }

    public void setRegistryConnectAddress(String registryConnectAddress) {
        this.registryConnectAddress = registryConnectAddress;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
