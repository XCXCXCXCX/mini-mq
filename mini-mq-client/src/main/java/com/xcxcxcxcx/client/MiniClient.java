package com.xcxcxcxcx.client;

import com.xcxcxcxcx.client.config.ClientConfig;
import com.xcxcxcxcx.client.connector.MiniConnectionClient;
import com.xcxcxcxcx.mini.api.client.Partner;
import com.xcxcxcxcx.registry.abs.DiscoveryClient;
import com.xcxcxcxcx.registry.abs.ServiceNode;
import com.xcxcxcxcx.registry.zookeeper.ZkDiscoveryClient;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class MiniClient {

    private final MiniConnectionClient client;

    private DiscoveryClient discoveryClient;

    public MiniClient(ClientConfig clientConfig, Partner partner) {
        InetSocketAddress hostAndPort = clientConfig.getHostAndPort();
        String connectString = clientConfig.getRegistryConnectAddress();
        String serviceName = clientConfig.getServiceName();
        if(hostAndPort == null && connectString == null){
            throw new IllegalArgumentException("Wrong Client Config : nodeAddress and registryAddress is null!");
        }

        String miniServerHost = hostAndPort.getHostString();
        int miniServerPort = hostAndPort.getPort();
        if(hostAndPort == null){
            discoveryClient = new ZkDiscoveryClient(connectString);
            discoveryClient.synStart();
            List<ServiceNode> serviceNodeList = discoveryClient.getInstances(serviceName);
            ServiceNode node = choose(serviceNodeList, "RANDOM");
            if(node == null){
                throw new RuntimeException("当前不存在可连接的broker节点...");
            }
            miniServerHost = node.getHost();
            miniServerPort = node.getPort();
        }
        client = new MiniConnectionClient(miniServerHost,
                miniServerPort,
                partner);
        client.start();
    }

    /**
     * 可以更优雅...
     * 偷个懒，先让项目跑起来
     * @param serviceNodeList
     * @param loadBalanceStrategy
     * @return
     */
    private ServiceNode choose(List<ServiceNode> serviceNodeList, String loadBalanceStrategy) {
        if(serviceNodeList == null || serviceNodeList.size() < 1){
            return null;
        }

        if("RANDOM".equals(loadBalanceStrategy)){
            Random random = new Random();
            int chooseIndex = random.nextInt(serviceNodeList.size());
            return serviceNodeList.get(chooseIndex);
        }
        return null;
    }

}
