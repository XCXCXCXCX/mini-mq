package com.xcxcxcxcx.mini.common.Executor;

import com.xcxcxcxcx.mini.api.spi.Spi;
import com.xcxcxcxcx.mini.api.spi.executor.ExecutorService;
import com.xcxcxcxcx.mini.api.spi.executor.config.ThreadPoolConfig;
import com.xcxcxcxcx.mini.tools.thread.ThreadPoolManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
@Spi(order = 1)
public final class ExecutorManager implements ExecutorService {

    private final Map<String, java.util.concurrent.Executor> executorMap = new ConcurrentHashMap<>();

    public ExecutorManager() {
        executorMap.put("Asyn-pull-to-memory-executor", Executors.newSingleThreadExecutor(new ThreadPoolManager("Asyn-pull-to-memory-pool")));
        executorMap.put("Asyn-retry-pull-to-memory-executor", Executors.newFixedThreadPool(5, new ThreadPoolManager("Asyn-retry-pull-to-memory-pool")));
    }

    @Override
    public java.util.concurrent.Executor get(ThreadPoolConfig config) {
        java.util.concurrent.Executor executor = executorMap.get(config.getName());
        if (executor != null) {
            return executor;
        }
        if ("fixed".equals(config.getType())) {
            executor = Executors.newFixedThreadPool(config.getMaxPoolSize(),
                    config.getThreadFactory());
            executorMap.put(config.getName(), executor);
            return executor;
        } else if ("cache".equals(config.getType())) {
            executor = Executors.newCachedThreadPool(config.getThreadFactory());
            executorMap.put(config.getName(), executor);
            return executor;
        } else if ("single".equals(config.getType())) {
            executor = Executors.newSingleThreadExecutor(config.getThreadFactory());
            executorMap.put(config.getName(), executor);
            return executor;
        } else if ("schedule".equals(config.getType())) {
            executor = Executors.newScheduledThreadPool(config.getCorePoolSize(),
                    config.getThreadFactory());
            executorMap.put(config.getName(), executor);
            return executor;
        } else {
            throw new IllegalArgumentException("暂时不支持创建该类型的线程池: type=" + config.getType());
        }
    }

    @Override
    public java.util.concurrent.Executor get(String name) {
        return executorMap.get(name);
    }

    @Override
    public Map<String, java.util.concurrent.Executor> getAll() {
        return executorMap;
    }
}
