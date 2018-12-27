package com.xcxcxcxcx.core;

import com.xcxcxcxcx.core.connector.MiniConnectionServer;
import com.xcxcxcxcx.mini.tools.config.MiniConfig;
import com.xcxcxcxcx.registry.abs.DiscoveryClient;
import com.xcxcxcxcx.registry.abs.ServiceNode;
import com.xcxcxcxcx.registry.zookeeper.CommonServiceNode;
import com.xcxcxcxcx.registry.zookeeper.ZkDiscoveryClientFactory;

import java.io.IOException;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class MiniServer {

    private static final String SERVER_HOST = MiniConfig.mini.registry.serverHost;
    private static final int SERVER_PORT = MiniConfig.mini.registry.serverPort;
    private static final String CONNECT_STRING = MiniConfig.mini.registry.connectString;

    private static final ServiceNode node;

    static {
        node = new CommonServiceNode();
        node.setHost(SERVER_HOST);
        node.setPort(SERVER_PORT);
    }

    private final MiniConnectionServer miniConnectionServer;

    private final DiscoveryClient discoveryClient;

    public MiniServer() {
        this.miniConnectionServer = new MiniConnectionServer(SERVER_HOST, SERVER_PORT);
        miniConnectionServer.synStart();
        this.discoveryClient = ZkDiscoveryClientFactory.create(CONNECT_STRING);
        discoveryClient.synStart();
        discoveryClient.register(node);
    }

    /**
     * 测试阶段暂时使用这种方式启动
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        new MiniServer();
        System.in.read();
    }

}
