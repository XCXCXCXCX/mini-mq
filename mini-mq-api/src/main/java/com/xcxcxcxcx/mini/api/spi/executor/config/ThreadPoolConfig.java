package com.xcxcxcxcx.mini.api.spi.executor.config;



import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;


/**
 *
 * 线程池配置
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ThreadPoolConfig implements RejectedExecutionHandler{

    private final String type;
    private final String name;//名字
    private final int corePoolSize; //核心线程大小
    private final int maxPoolSize; //最大线程大小
    private final int queueCapacity;  // 允许缓冲在队列中的任务数 (0:不缓冲、负数：无限大、正数：缓冲的任务数)
    private final int keepAliveSeconds;// 存活时间
    private final ThreadFactory threadFactory;
    private final RejectedExecutionHandler rejectedExecutionHandler;

    private static final int DEFAULT_CORE_POOL_SIZE = 10;
    private static final int DEFAULT_MAX_POOL_SIZE = 100;
    private static final int DEFAULT_QUEUE_CAPACITY = 50;
    private static final int DEFAULT_KEEP_ALIVE_SECONDS = 120;

    /**
     * 线程池名称，线程生产工厂，两个必须的参数
     * @param name
     * @param threadFactory
     */
    public ThreadPoolConfig(String type, String name, ThreadFactory threadFactory) {
        this.type = type;
        this.name = name;
        this.corePoolSize = DEFAULT_CORE_POOL_SIZE;
        this.maxPoolSize = DEFAULT_MAX_POOL_SIZE;
        this.queueCapacity = DEFAULT_QUEUE_CAPACITY;
        this.keepAliveSeconds = DEFAULT_KEEP_ALIVE_SECONDS;
        this.threadFactory = threadFactory;
        this.rejectedExecutionHandler = this;
    }

    /**
     * 自定义配置1
     * @param name
     * @param corePoolSize
     * @param maxPoolSize
     * @param queueCapacity
     * @param keepAliveSeconds
     * @param threadFactory
     */
    public ThreadPoolConfig(String type, String name, int corePoolSize, int maxPoolSize, int queueCapacity, int keepAliveSeconds, ThreadFactory threadFactory) {
        this.type = type;
        this.name = name;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.queueCapacity = queueCapacity;
        this.keepAliveSeconds = keepAliveSeconds;
        this.threadFactory = threadFactory;
        this.rejectedExecutionHandler = this;
    }

    /**
     * 自定义配置2
     * @param name
     * @param corePoolSize
     * @param maxPoolSize
     * @param queueCapacity
     * @param keepAliveSeconds
     * @param threadFactory
     * @param rejectedExecutionHandler
     */
    public ThreadPoolConfig(String type, String name, int corePoolSize, int maxPoolSize, int queueCapacity, int keepAliveSeconds, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        this.type = type;
        this.name = name;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.queueCapacity = queueCapacity;
        this.keepAliveSeconds = keepAliveSeconds;
        this.threadFactory = threadFactory;
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        throw new RejectedExecutionException("one task rejected, pool=" + this.getName());
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }
}
