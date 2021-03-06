package com.xcxcxcxcx.registry.zookeeper;

import com.xcxcxcxcx.registry.abs.ServiceNode;

import java.util.UUID;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class CommonServiceNode implements ServiceNode {

    private static final String SERVER_NAME = "brokers";

    private String serviceName = SERVER_NAME;

    private String id;

    private String host;

    private int port;

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 这里由于没有并发getId的场景，所以不需要同步
     * @return
     */
    @Override
    public String getId() {
        if(id == null){
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "CommonServiceNode{" +
                "serviceName='" + serviceName + '\'' +
                ", id='" + id + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
