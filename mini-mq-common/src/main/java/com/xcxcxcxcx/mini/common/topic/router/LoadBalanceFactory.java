package com.xcxcxcxcx.mini.common.topic.router;

import com.xcxcxcxcx.mini.api.connector.topic.router.LoadBalance;

import java.util.HashMap;
import java.util.Map;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class LoadBalanceFactory {

    private static final Map<LoadBalance.LoadBalanceStrategy, Class> loadBalanceMap = new HashMap<>();

    static {
        loadBalanceMap.put(LoadBalance.LoadBalanceStrategy.MAX_LB, MaxLoadBalance.class);
        loadBalanceMap.put(LoadBalance.LoadBalanceStrategy.MIN_LB, MinLoadBalance.class);
        loadBalanceMap.put(LoadBalance.LoadBalanceStrategy.POLLING, PollingLoadBalance.class);
        loadBalanceMap.put(LoadBalance.LoadBalanceStrategy.RANDOM, RandomLoadBalance.class);
    }

    public static LoadBalance get(LoadBalance.LoadBalanceStrategy strategy){
        Class clazz = loadBalanceMap.get(strategy);
        if(clazz == null){
            throw new IllegalArgumentException("目前还不支持该负载策略" + strategy.getName());
        }
        try {
            return (LoadBalance) clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("LoadBalance实例化失败");
    }

}
