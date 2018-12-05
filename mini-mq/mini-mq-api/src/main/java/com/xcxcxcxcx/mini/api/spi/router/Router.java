package com.xcxcxcxcx.mini.api.spi.router;

import com.xcxcxcxcx.mini.api.connector.topic.router.LoadBalance;

import java.util.List;


/**
 *
 * 路由器
 * @author XCXCXCXCX
 * @Since 1.0
 */
public interface Router<T> {


    /**
     * 从list中路由出一个
     * @param lbStrategy
     * @param tList
     * @return
     */
    T route(LoadBalance.LoadBalanceStrategy lbStrategy, List<T> tList);

    /**
     * hash(key) 后选择
     * @param tList
     * @param key
     * @return
     */
    T route(List<T> tList, String key);
}
