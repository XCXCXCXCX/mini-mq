package com.xcxcxcxcx.mini.api.spi.executor;

import com.xcxcxcxcx.mini.api.spi.executor.config.ThreadPoolConfig;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 *
 * 线程池
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface ExecutorService {

    Executor get(ThreadPoolConfig config);

    Executor get(String name);

    Map<String, Executor> getAll();

}
