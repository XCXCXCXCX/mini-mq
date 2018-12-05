package com.xcxcxcxcx.mini.api.connector.topic.router;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * 负载均衡规则
 * @author XCXCXCXCX
 * @Since 1.0
 */
public interface LoadBalance {

    default Set<Integer> hash(int size, String... keys){
        Set<Integer> keyIndex = new HashSet<>();
        for (String key: keys){
            keyIndex.add(key.hashCode()&size);
        }
        return keyIndex;
    }

    /**
     * 根据路由key来分配
     * @param keys
     * @return
     */
    int choose(List<Integer> list, String... keys);

    /**
     * 根据可选列表来选择
     * @param list
     * @return
     */
    int choose(List<Integer> list);


    enum LoadBalanceStrategy{
        RANDOM("随机"),
        POLLING("轮询"),
        WEIGHT_POLLING("加权轮询"),
        MAX_LB("最大负载"),
        MIN_LB("最小负载");

        private String name;

        LoadBalanceStrategy(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
