package com.xcxcxcxcx.mini.common.topic.router;

import com.xcxcxcxcx.mini.api.connector.topic.router.LoadBalance;
import com.xcxcxcxcx.mini.api.spi.Spi;
import com.xcxcxcxcx.mini.api.spi.router.Router;
import com.xcxcxcxcx.mini.common.topic.BasePartition;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * partition的路由器
 * @author XCXCXCXCX
 * @since 1.0
 */
@Spi(order = 1)
public class DefaultPartitionRouter implements Router<BasePartition>{

    /**
     * 选择最大负载或最小负载
     * @param lbStrategy
     * @param BasePartitions
     * @return
     */
    @Override
    public BasePartition route(LoadBalance.LoadBalanceStrategy lbStrategy, List<BasePartition> BasePartitions) {

        LoadBalance lb = LoadBalanceFactory.get(lbStrategy);

        List<Integer> partitionSubscribeNum = BasePartitions.stream()
                .map(p->p.getSubscribeNum()).collect(Collectors.toList());
        int chooseIndex = lb.choose(partitionSubscribeNum);

        return BasePartitions.get(chooseIndex);
    }

    /**
     * 用hash key路由
     * @param basePartitions
     * @param key
     * @return
     */
    @Override
    public BasePartition route(List<BasePartition> basePartitions, String key) {
        int choosePartitionIndex = key.hashCode() & basePartitions.size();
        return basePartitions.get(choosePartitionIndex);
    }
}
