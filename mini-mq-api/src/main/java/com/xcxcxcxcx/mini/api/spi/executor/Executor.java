package com.xcxcxcxcx.mini.api.spi.executor;

import com.xcxcxcxcx.mini.api.spi.executor.config.ThreadPoolConfig;

/**
 *
 * 线程池
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface Executor {

    java.util.concurrent.Executor get(ThreadPoolConfig config);

    java.util.concurrent.Executor get(String name);

}
