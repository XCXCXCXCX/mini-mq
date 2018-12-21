package com.xcxcxcxcx.client.config;

import java.net.InetSocketAddress;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ClientConfig {

    public static final String DEFAULT_SERVICE_NAME = "brokers";

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

    public static class ClientConfigBuilder {

        private String host;

        private final int port;

        private String connectString;

        public ClientConfigBuilder(int port) {
            this.port = port;
        }

        public ClientConfigBuilder setHost(String host){
            this.host = host;
            return this;
        }

        public ClientConfigBuilder setRegistryConnectAddress(String connectString){
            this.connectString = connectString;
            return this;
        }

        public ClientConfig build(){
            ClientConfig clientConfig = new ClientConfig();
            InetSocketAddress address = host == null? new InetSocketAddress(port) : new InetSocketAddress(host, port);
            clientConfig.setHostAndPort(address);
            if(connectString != null){
                clientConfig.setRegistryConnectAddress(connectString);
            }
            return clientConfig;
        }

    }
}
