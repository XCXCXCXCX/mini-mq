package com.xcxcxcxcx.registry.zookeeper;

import com.xcxcxcxcx.mini.api.event.service.BaseService;
import com.xcxcxcxcx.mini.api.spi.json.JsonSerializationService;
import com.xcxcxcxcx.mini.api.spi.json.JsonSerializationServiceFactory;
import com.xcxcxcxcx.registry.abs.DiscoveryClient;
import com.xcxcxcxcx.registry.abs.ServiceNode;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ZkDiscoveryClient extends BaseService implements DiscoveryClient{

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkDiscoveryClient.class);

    private static final String ROOT_PATH = "/mini/";

    private final JsonSerializationService jsonService = JsonSerializationServiceFactory.create();

    private CuratorFramework zkClient;

    private final String connectString;

    public ZkDiscoveryClient(String connectString) {
        this.connectString = connectString;
    }

    @Override
    public void init() {
        if(connectString == null){
            throw new IllegalArgumentException("zk connectString is null!");
        }else{
            zkClient = CuratorManager.getClient(connectString);
        }
    }

    @Override
    public void destroy() {
        zkClient = null;
    }

    @Override
    public void doStart() {
        zkClient.start();
        LOGGER.info("zkClient start success.");
    }

    @Override
    public void doStop() {
        zkClient.close();
        LOGGER.info("zkClient closed.");
    }

    @Override
    public List<ServiceNode> getInstances(String serviceName) {
        String path = ROOT_PATH + serviceName;
        try {
            List<String> childNodes = zkClient.getChildren().forPath(path);
            return childNodes.stream()
                    .map(childNode -> {

                        try {
                            String node = new String(zkClient.getData().forPath(path +'/' + childNode));
                            return jsonService.parseObject(node, CommonServiceNode.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void register(ServiceNode node) {
        String key = ROOT_PATH + node.getNodePath();
        byte[] value = jsonService.parseString(node).getBytes();
        try {
            if(zkClient.checkExists().forPath(key) != null){
                zkClient.delete().deletingChildrenIfNeeded().forPath(key);
                LOGGER.info("服务已存在，更新成功!");
            }
            zkClient.create().creatingParentContainersIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(key, value);
            LOGGER.info("服务注册成功!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregister(ServiceNode node) {
        String key = ROOT_PATH + node.getNodePath();
        try {
            if(zkClient.checkExists().forPath(key) != null){
                zkClient.delete().deletingChildrenIfNeeded().forPath(key);
                LOGGER.info("服务注销成功!");
            }else{
                LOGGER.info("服务不存在，无法注销：{}", key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
